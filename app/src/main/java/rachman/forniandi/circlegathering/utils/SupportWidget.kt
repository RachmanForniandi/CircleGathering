package rachman.forniandi.circlegathering.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.stackWidgets.MyStoryStackWidget

object SupportWidget {
    fun notifyDataSetChanged(context: Context) {
        Log.i("TEST_UPDATE_WIDGET", "Requested update data for widget")
        val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
        val widgetIds = widgetManager.getAppWidgetIds(
            ComponentName(context.applicationContext, MyStoryStackWidget::class.java)
        )

        /*val intentWidgetNotify = Intent(context, MyStoryStackWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        }
        context.sendBroadcast(intentWidgetNotify)*/
        //widgetIds
        widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.stack_view)
    }

    suspend fun <T> Flow<List<T>>.toArrayList(): ArrayList<T> {
        val arrayList = ArrayList<T>()
        collect { list ->
            arrayList.clear() // Clear the ArrayList to ensure we have a fresh copy of the data
            arrayList.addAll(list) // Add all elements from the Flow's emitted list to the ArrayList
        }
        return arrayList
    }
}