package rachman.forniandi.circlegathering.stackWidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.activities.DetailStoryActivity
import rachman.forniandi.circlegathering.activities.SplashScreenActivity
import rachman.forniandi.circlegathering.utils.ConstantsMain
import rachman.forniandi.circlegathering.utils.SupportWidget
import rachman.forniandi.circlegathering.utils.supportWorker.CoreWorker
import java.util.concurrent.TimeUnit


class MyStoryStackWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val intentFirst =Intent(context, StoryStackWidgetService::class.java)
        intentFirst.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intentFirst.data = intentFirst.toUri(Intent.URI_INTENT_SCHEME).toUri()

        val viewsWidget = RemoteViews(context.packageName, R.layout.my_story_stack_widget)
        viewsWidget.setRemoteAdapter(R.id.stack_view,intentFirst)
        viewsWidget.setEmptyView(R.id.stack_view,R.id.empty_view)

        initiateStackItems(viewsWidget, context, appWidgetId)
        initiateClickLabelWidgetToApps(viewsWidget, context)
        initiateClickRefresh(viewsWidget, context)


         appWidgetManager.updateAppWidget(appWidgetId, viewsWidget)
    }
    private fun initiateStackItems(views: RemoteViews, context: Context, appWidgetId: Int) {
        val clickIntentForUpdate = Intent(context, StoryStackWidgetService::class.java)
        clickIntentForUpdate.action= CLICK_ACTION_WIDGET
        clickIntentForUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        val clickPendingIntentForUpdate = PendingIntent.getBroadcast(
            context,
            0,
            clickIntentForUpdate,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else 0
        )
        views.setPendingIntentTemplate(R.id.stack_view,clickPendingIntentForUpdate)

    }
    private fun initiateClickRefresh(views: RemoteViews, context: Context) {
        val refreshIntent = Intent(context, MyStoryStackWidget::class.java)
        refreshIntent.action = BUTTON_REFRESH_WIDGET_CLICK
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, refreshIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else 0
        )
        views.setOnClickPendingIntent(R.id.btn_refresh_widget, pendingIntent)
    }

    private fun initiateClickLabelWidgetToApps(views: RemoteViews, context: Context) {
        val labelNameIntent = Intent(context, MyStoryStackWidget::class.java)
        labelNameIntent.action = CLICK_LABEL_TITLE_WIDGET
        val holdIntent = PendingIntent.getBroadcast(
            context, 0, labelNameIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else 0
        )
        views.setOnClickPendingIntent(R.id.banner_text, holdIntent)

    }


    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action != null) {
            when(intent.action){
                CLICK_ACTION_WIDGET->{
                    val bundleKey = intent.getStringExtra(KEY_EXTRA_ITEM)
                    val mIntent = Intent(context, DetailStoryActivity::class.java)
                    mIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    mIntent.putExtra(DetailStoryActivity.EXTRA_ID_DETAIL_STORY, bundleKey)

                    context?.startActivity(mIntent)
                }
                BUTTON_REFRESH_WIDGET_CLICK->{
                    context?.let {
                        Handler(it.mainLooper).post {
                            Toast.makeText(context, "Data Widget Dimuat", Toast.LENGTH_SHORT).show()
                            SupportWidget.notifyDataSetChanged(context)
                        }
                    }
                }
                CLICK_LABEL_TITLE_WIDGET->{
                    val intentToApp = Intent(context, SplashScreenActivity::class.java)
                    intentToApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context?.startActivity(intentToApp)
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        val workerSupport = WorkManager.getInstance(context)

        val constraintBuilder = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val settingWorkRequest = PeriodicWorkRequestBuilder<CoreWorker>(30, TimeUnit.MINUTES)
            .setConstraints(constraintBuilder)
            .build()

        workerSupport.enqueueUniquePeriodicWork(
            ConstantsMain.KEY_FOR_WIDGET_WORKER,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            settingWorkRequest

        )
    }

    override fun onDisabled(context: Context) {
        val workerDisabled = WorkManager.getInstance(context)
        workerDisabled.cancelUniqueWork(ConstantsMain.KEY_FOR_WIDGET_WORKER)
    }

    companion object{
        private const val CLICK_ACTION_WIDGET = "rachman.forniandi.circlegathering.CLICK_ACTION_WIDGET"
        const val KEY_EXTRA_ITEM = "key_extra_item"
        const val BUTTON_REFRESH_WIDGET_CLICK= "REFRESH_WIDGET_DATA"
        const val CLICK_LABEL_TITLE_WIDGET = "USER_CLICK_WIDGET_TITLE"




    }
}



