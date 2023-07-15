package rachman.forniandi.circlegathering.DBRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity


@Database(
    entities = [StoriesEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StoriesDatabase:RoomDatabase() {
    abstract fun storiesDao():StoriesDao
}