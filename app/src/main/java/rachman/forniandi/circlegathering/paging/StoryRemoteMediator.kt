package rachman.forniandi.circlegathering.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import rachman.forniandi.circlegathering.dBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.dBRoom.entities.RemoteKeys
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.ConstantsMain

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoriesDatabase,
    private val remoteDataSource: RemoteDataSource,
    private val token: String
): RemoteMediator<Int, StoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeysForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        return try {
            val responseStoryData = remoteDataSource.showStoriesPerPages(makeBearerToken(token),page,state.config.pageSize)
            val endOfPagination = responseStoryData.body()?.listStory?.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storiesDao().deleteAllStories()
                    database.remoteKeysFromDao().deleteAllKeys()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPagination == true) null else page + 1
                val keys = responseStoryData.body()?.listStory?.map {
                    it.id?.let { id -> RemoteKeys(id = id, prevKey = prevKey, nextKey = nextKey) }
                }
                database.remoteKeysFromDao().insertAllKeys(keys)
                responseStoryData.body()?.let { database.storiesDao().insertStories(it.listStory) }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPagination == true)
        }catch (exception: Exception) {
            MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, StoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysFromDao().getRemoteKeysById(data.id!!)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysFromDao().getRemoteKeysById(data.id!!)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysFromDao().getRemoteKeysById(id)
            }
        }
    }

    private fun makeBearerToken(token: String): String {
        return ConstantsMain.TOKEN_BEARER+token
    }

}
