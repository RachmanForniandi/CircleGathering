package rachman.forniandi.circlegathering.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.dBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.paging.StoryRemoteMediator
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.ConstantsMain
import javax.inject.Inject

@ExperimentalPagingApi
class MainNewRepository @Inject constructor(
    private val dbStories: StoriesDatabase,
    private val remoteSource: RemoteDataSource) {

    fun getAllStoriesPerPage(token: String): Flow<PagingData<StoryItem>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            remoteMediator = StoryRemoteMediator(
                dbStories,
                remoteSource,
                makeBearerToken(token)
            ),
            pagingSourceFactory = {
                dbStories.storiesDao().readAllStories()
            }
        ).flow
    }

    private fun makeBearerToken(token: String): String {
        return ConstantsMain.TOKEN_BEARER+token
    }
}



