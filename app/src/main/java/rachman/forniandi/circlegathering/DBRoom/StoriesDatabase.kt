package rachman.forniandi.circlegathering.DBRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity


@Database(
    entities = [StoriesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StoriesTypeConverter::class)
abstract class StoriesDatabase:RoomDatabase() {
    abstract fun storiesDao():StoriesDao
}