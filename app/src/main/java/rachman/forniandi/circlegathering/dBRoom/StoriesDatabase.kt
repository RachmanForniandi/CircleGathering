package rachman.forniandi.circlegathering.dBRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import rachman.forniandi.circlegathering.dBRoom.entities.RemoteKeys
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity


@Database(
    entities = [StoriesEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StoriesTypeConverter::class)
abstract class StoriesDatabase:RoomDatabase() {
    abstract fun storiesDao():StoriesDao
    abstract fun remoteKeysFromDao(): KeyRemoteDao


}
