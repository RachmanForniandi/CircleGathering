package rachman.forniandi.circlegathering.dBRoom

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import rachman.forniandi.circlegathering.models.allStories.StoryItem

@Dao
interface StoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(vararg story: List<StoryItem>)

    @Query("SELECT * FROM story_table ORDER BY id ASC")
    fun readAllStories(): PagingSource<Int, StoryItem>

    @Query("DELETE FROM story_table ")
    suspend fun deleteAllStories()

}
