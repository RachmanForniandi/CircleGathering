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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import rachman.forniandi.circlegathering.DBRoom.StoriesDao
import rachman.forniandi.circlegathering.DBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.models.allStories.ListStoryItem

class StoryStackRemoteViewFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private var dbRoom:StoriesDatabase = StoriesDatabase(mContext)
    private var daoData:StoriesDao=dbRoom.storiesDao()
    private val mWidgetItems = arrayListOf<Bitmap>()
    //private val mData = arrayListOf<ListStoryItem>()
    private lateinit var stories : List<StoriesEntity>

    override fun onCreate() {
        //not used yet
        //daoData =dbRoom.storiesDao()
        //fetchDataFromDbRoom()
    }

    /*private fun fetchDataFromDbRoom() {
        runBlocking {
            //stories = repository.localMain.readDbStories().flatMapConcat { it.asFlow() }.toList()
            //stories = dao.readStories().flatMapConcat { it.asFlow() }.toList()
            stories = daoData.readStories().flatMapConcat { it.asFlow() }.toList()
            Log.d("debugRoom",""+stories)
        }

    }*/


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onDataSetChanged(){
        /*fetchDataFromDbRoom()

        println("checkMethodRoom" + fetchDataFromDbRoom())*/
        CoroutineScope(Dispatchers.Main.immediate).launch  {
            //stories = repository.localMain.readDbStories().flatMapConcat { it.asFlow() }.toList()
            //stories = dao.readStories().flatMapConcat { it.asFlow() }.toList()
            stories = daoData.readStories().flatMapConcat { it.asFlow() }.toList()
            Log.d("debugRoom",""+ stories)
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
        return mWidgetItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViewItems = RemoteViews(mContext.packageName, R.layout.item_widget_story)
        val storiesItem = stories[position].responseAllStories.listStory

        try {

            val bitmap: Bitmap = Glide.with(mContext)
                .asBitmap()
                .load(storiesItem.get(0).photoUrl)
                .submit()
                .get()

            mWidgetItems.addAll(listOf(bitmap))
            remoteViewItems.setImageViewBitmap(R.id.iv_widget,mWidgetItems[position])

        }catch (e:Exception){
            Handler(mContext.mainLooper).post {
                Toast.makeText(
                    mContext,

                    StringBuilder(mContext.getString(R.string.msg_text_failed_fetch_data))
                        .append(" : ")
                        .append(e.message),
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("test_widget", "Failed fetch data : ${e.message}")
                e.printStackTrace()
            }
        }

        val keyExtras = bundleOf(MyStoryStackWidget.KEY_EXTRA_ITEM to stories[position].id)

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