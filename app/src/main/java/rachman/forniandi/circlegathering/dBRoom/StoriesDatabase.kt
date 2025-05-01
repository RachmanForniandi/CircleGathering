package rachman.forniandi.circlegathering.dBRoom

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import rachman.forniandi.circlegathering.dBRoom.entities.RemoteKeys
import rachman.forniandi.circlegathering.di.DatabaseModule
import rachman.forniandi.circlegathering.models.allStories.StoryItem


@Database(
    entities = [StoryItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StoriesTypeConverter::class)
abstract class StoriesDatabase:RoomDatabase() {
    abstract fun storiesDao():StoriesDao
    abstract fun remoteKeysFromDao(): KeyRemoteDao


    /*companion object {
        @Volatile
        private var instanceDb: StoriesDatabase? = null
        private val LOCK = Any()

        fun getInstanceDb(context: Context) =
            instanceDb ?: synchronized(this) {
                instanceDb ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoriesDatabase::class.java,
                    "stories_db"
                ).build()
            }.also {
                instanceDb = it
            }
        *//*operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: DatabaseModule.provideDatabase(context).also { instance = it }
        }*//*

    }*/

}
