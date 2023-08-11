package rachman.forniandi.circlegathering.source

import android.util.Log
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.DBRoom.StoriesDao
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val storiesDao: StoriesDao
){
    suspend fun insertStories(storiesEntity: StoriesEntity){
        return storiesDao.insertStories(storiesEntity)
    }

    fun readDbStories(): Flow<List<StoriesEntity>>{
        return storiesDao.readStories()
        Log.d("testRead","${storiesDao.readStories()}")
    }
}