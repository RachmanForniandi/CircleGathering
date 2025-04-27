/*
package rachman.forniandi.circlegathering.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.source.LocalDataSource
import rachman.forniandi.circlegathering.source.RemoteDataSource

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val token: String
): RemoteMediator<Int, ResponseAllStories>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ResponseAllStories>
    ): MediatorResult {
        TODO("Not yet implemented")
    }

}*/
