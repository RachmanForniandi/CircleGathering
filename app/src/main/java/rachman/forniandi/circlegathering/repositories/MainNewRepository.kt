package rachman.forniandi.circlegathering.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.dBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.paging.StoryRemoteMediator
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.ConstantsMain
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.addedBearerToToken
import javax.inject.Inject

@ExperimentalPagingApi
class MainNewRepository @Inject constructor(
    private val dbStories: StoriesDatabase,
    private val remoteSource: RemoteDataSource) {

    fun getAllStoriesPerPage(token: String): Flow<PagingData<StoriesEntity>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            remoteMediator = StoryRemoteMediator(
                dbStories,
                remoteSource,
                token
            ),
            pagingSourceFactory = {
                dbStories.storiesDao().readAllStories()
            }
        ).flow
    }

}



