package rachman.forniandi.circlegathering.stackWidgets

import android.content.Intent
import android.widget.RemoteViewsService
import rachman.forniandi.circlegathering.DBRoom.StoriesDao
import rachman.forniandi.circlegathering.DBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.repositories.MainRepository
import javax.inject.Inject

class StoryStackWidgetService : RemoteViewsService() {

   // @Inject
    //var storiesDatabase= StoriesDatabase(applicationContext)
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return StoryStackRemoteViewFactory(this.applicationContext)
    }
}
