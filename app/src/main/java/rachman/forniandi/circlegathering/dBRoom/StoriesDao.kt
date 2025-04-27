package rachman.forniandi.circlegathering.dBRoom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.models.allStories.StoryItem

/*
@Dao
interface StoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(story: List<StoryItem>)

    @Query("SELECT * FROM story_table ORDER BY id ASC")
    fun readStories(): Flow<List<StoryItem>>

    @Query("DELETE FROM story_table ")
    fun deleteAllStories()

}*/
