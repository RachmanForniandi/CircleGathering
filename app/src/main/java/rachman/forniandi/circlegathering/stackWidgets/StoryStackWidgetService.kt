package rachman.forniandi.circlegathering.stackWidgets

import android.content.Intent
import android.widget.RemoteViewsService

class StoryStackWidgetService : RemoteViewsService() {


    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return StoryStackRemoteViewFactory(this.applicationContext)
    }
}
