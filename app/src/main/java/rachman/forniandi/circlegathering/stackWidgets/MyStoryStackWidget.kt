package rachman.forniandi.circlegathering.stackWidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.activities.DetailStoryActivity
import rachman.forniandi.circlegathering.utils.ConstantsMain
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 */
/*class MyStoryStackWidget : AppWidgetProvider() {
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

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action != null) {
            if (intent.action == CLICK_ACTION_WIDGET) {
                val storyKey = intent.getStringExtra(KEY_EXTRA_ITEM)
                val mIntent = Intent(context, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.EXTRA_ID_DETAIL_STORY, storyKey)
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    mIntent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    else PendingIntent.FLAG_UPDATE_CURRENT
                )
                pendingIntent.send()
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


        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            val intentFirst =Intent(context, StoryStackWidgetService::class.java)
            val viewsWidget = RemoteViews(context.packageName, R.layout.my_story_stack_widget)
            viewsWidget.setRemoteAdapter(R.id.stack_view,intentFirst)
            viewsWidget.setEmptyView(R.id.stack_view,R.id.empty_view)


            val clickIntentForUpdate = Intent(context, StoryStackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                action = CLICK_ACTION_WIDGET
            }
            val clickPendingIntentForUpdate = PendingIntent.getBroadcast(
                context,
                0,
                clickIntentForUpdate,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                } else 0
            )
            // Instruct the widget manager to update the widget
            viewsWidget.setPendingIntentTemplate(R.id.stack_view,clickPendingIntentForUpdate)
            appWidgetManager.updateAppWidget(appWidgetId, viewsWidget)
        }

        fun notifyDataSetChanged(context: Context) {
            val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val widgetIds = widgetManager.getAppWidgetIds(
                ComponentName(context.applicationContext, MyStoryStackWidget::class.java)
            )

            val intentWidgetNotify = Intent(context,MyStoryStackWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
            }
            context.sendBroadcast(intentWidgetNotify)

        }
    }
}*/



