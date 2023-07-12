package rachman.forniandi.circlegathering.stackWidgets

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.models.allStories.ListStoryItem

internal class StoryStackRemoteViewFactory(private val mContext: Context,
private val widgetUseCase: SourceForWidgetStoryUseCase) : RemoteViewsService.RemoteViewsFactory {
    private val storyItemBitmap = arrayListOf<Bitmap>()
    private val stories = arrayListOf<ListStoryItem>()

    override fun onCreate() {
    }

    override fun onDataSetChanged() = runBlocking {
        try {
            val resultData = widgetUseCase.callDataStories().first()
            val bitmapDisplay = resultData.map {
                Glide.with(mContext)
                    .asBitmap()
                    .load(it.photoUrl)
                    .override(256, 256)
                    .submit()
                    .get()
            }
            storyItemBitmap.clear()
            stories.clear()
            storyItemBitmap.addAll(bitmapDisplay)
            stories.addAll(resultData)

        }catch (e:Exception){
            e.printStackTrace()
        }
        MyStoryStackWidget.notifyDataSetChanged(mContext)
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return stories.size
    }

    override fun getViewAt(positionItem: Int): RemoteViews {
        val remoteViewItems = RemoteViews(mContext.packageName, R.layout.item_widget_story).apply {
            setImageViewBitmap(R.id.iv_widget, storyItemBitmap[positionItem])
        }

        val keyExtras = bundleOf(MyStoryStackWidget.KEY_EXTRA_ITEM to stories[positionItem].id)

        val prepareIntent = Intent().putExtras(keyExtras)

        remoteViewItems.setOnClickFillInIntent(R.id.iv_widget,prepareIntent)
        return remoteViewItems
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

}