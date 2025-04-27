/*
package rachman.forniandi.circlegathering.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.networkUtil.NetworkService
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.ConstantsMain

class StoryPagingResource(private val networkService: NetworkService) : PagingSource<Int, ResponseAllStories>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ResponseAllStories>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResponseAllStories> {
        TODO("Not yet implemented")
        */
/*return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = networkService.getAllStoriesNew()
        }*//*

    }

    private fun makeBearerToken(token: String): String {
        return ConstantsMain.TOKEN_BEARER+token
    }
}*/
