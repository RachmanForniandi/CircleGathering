package rachman.forniandi.circlegathering.DBRoom

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.di.DatabaseModule
import rachman.forniandi.circlegathering.di.DatabaseModule_ProvideDatabaseFactory


@Database(
    entities = [StoriesEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(StoriesTypeConverter::class)
abstract class StoriesDatabase:RoomDatabase() {
    abstract fun storiesDao():StoriesDao


    companion object {
        @Volatile
        private var instance: StoriesDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: DatabaseModule.provideDatabase(context).also { instance = it }
        }
    }
}