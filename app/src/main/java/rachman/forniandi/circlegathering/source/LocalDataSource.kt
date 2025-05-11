package rachman.forniandi.circlegathering.source

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.dBRoom.KeyRemoteDao
import rachman.forniandi.circlegathering.dBRoom.StoriesDao
import rachman.forniandi.circlegathering.dBRoom.entities.RemoteKeys
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val storiesDao: StoriesDao,
    private val remoteKeysDao: KeyRemoteDao
){
    suspend fun insertStories(storiesEntity: List<StoriesEntity>){
        return storiesDao.insertStories(storiesEntity)
    }

    suspend fun readDbStories(): PagingSource<Int, StoriesEntity> {
        return storiesDao.readAllStories()
    }

    suspend fun deleteAllStories(){
        return storiesDao.deleteAllStories()
    }

    suspend fun insertKey(key: List<RemoteKeys>){
        return remoteKeysDao.insertAllKeys(key)
    }

    suspend fun readKey(id: String): RemoteKeys {
        return remoteKeysDao.getRemoteKeysById(id)
    }

    suspend fun deleteKey(){
        return remoteKeysDao.deleteAllKeys()
    }




}
