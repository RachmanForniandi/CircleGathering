package rachman.forniandi.circlegathering.dBRoom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import rachman.forniandi.circlegathering.dBRoom.entities.RemoteKeys

@Dao
interface KeyRemoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKeys(story: List<RemoteKeys?>?)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysById(id: String): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteAllKeys()
}

