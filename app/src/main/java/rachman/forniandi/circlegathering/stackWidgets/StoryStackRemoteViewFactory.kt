package rachman.forniandi.circlegathering.stackWidgets



import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import rachman.forniandi.circlegathering.DBRoom.StoriesDao
import rachman.forniandi.circlegathering.DBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.models.allStories.ListStoryItem
import rachman.forniandi.circlegathering.utils.SupportWidget
import rachman.forniandi.circlegathering.utils.SupportWidget.toArrayList

internal class StoryStackRemoteViewFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var dbRoom:StoriesDatabase
    private lateinit var daoData:StoriesDao
    private val mWidgetItems = arrayListOf<Bitmap>()
    private val mData = arrayListOf<ListStoryItem>()
    private lateinit var stories : Flow<List<StoriesEntity>>

    override fun onCreate() {
        dbRoom = StoriesDatabase(mContext)
        daoData= dbRoom.storiesDao()
    }


    override fun onDataSetChanged() {
        runBlocking  {
                stories = daoData.readStories()
                val storiesToArray= stories.toArrayList()
                val storiesItem = storiesToArray[0].responseAllStories.listStory

                Log.d("debugDao", "" +storiesToArray)
                try {
                    val bitmap: Bitmap = Glide.with(mContext)
                        .asBitmap()
                        .placeholder(R.drawable.place_holder)
                        .error(R.drawable.error_placeholder)
                        .load(storiesItem[0].photoUrl)
                        .sizeMultiplier(0.6f)
                        .submit()
                        .get()

                    mWidgetItems.addAll(listOf(bitmap))
                    mData.addAll(storiesItem)
                    Log.d("StoryWidget", "onDataSetChanged: Received ${mData.size} items")
                }catch (e:Exception){
                    Handler(mContext.mainLooper).post {
                        Toast.makeText(
                            mContext,
                            StringBuilder(mContext.getString(R.string.msg_text_failed_fetch_data))
                                .append(" : ")
                                .append(e.message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    e.printStackTrace()
                    Log.e("StoryWidget", "Error in onDataSetChanged", e)

                }
                SupportWidget.notifyDataSetChanged(mContext)

        }

    }

    override fun onDestroy() {
        Handler(mContext.mainLooper).post {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.msg_text_widget_removed),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViewItems = RemoteViews(mContext.packageName, R.layout.item_widget_story)
        remoteViewItems.setImageViewBitmap(R.id.iv_widget,mWidgetItems[position])

        val keyExtras = bundleOf(MyStoryStackWidget.KEY_EXTRA_ITEM to mWidgetItems[position])

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
        return 0L
    }

    override fun hasStableIds(): Boolean {
        return false
    }

}