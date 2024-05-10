package rachman.forniandi.circlegathering.DBRoom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity

@Dao
interface StoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(storiesEntity: StoriesEntity)

    @Query("SELECT * FROM story_table ORDER BY id ASC")
    fun readStories(): Flow<List<StoriesEntity>>

}