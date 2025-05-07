package rachman.forniandi.circlegathering.dBRoom

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity

@Dao
interface StoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(story: List<StoriesEntity?>?)

    @Query("SELECT * FROM story_table ORDER BY id DESC")
    fun readAllStories(): PagingSource<Int, StoriesEntity>

    @Query("DELETE FROM story_table ")
    suspend fun deleteAllStories()

}
