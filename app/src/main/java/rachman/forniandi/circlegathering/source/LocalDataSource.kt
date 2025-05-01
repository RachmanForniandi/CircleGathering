package rachman.forniandi.circlegathering.source

import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.dBRoom.StoriesDao
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val storiesDao: StoriesDao
){
    /*suspend fun insertStories(storiesEntity: List<StoryItem>){
        return storiesDao.insertStories(storiesEntity)
    }

    *//*fun readDbStories(): Flow<List<StoryItem>>{
        return storiesDao.readStories()
    }*//*

    fun deleteAllStories(){
        return storiesDao.deleteAllStories()
    }*/
}
