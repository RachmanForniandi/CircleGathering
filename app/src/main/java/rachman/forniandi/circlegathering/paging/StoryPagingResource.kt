package rachman.forniandi.circlegathering.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.ConstantsMain

class StoryPagingResource(private val remoteDataSource: RemoteDataSource,private val token: String) : PagingSource<Int, StoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {

    return try {
        val position = params.key ?: INITIAL_PAGE_INDEX
        val responseData = remoteDataSource.showStoriesPerPages(makeBearerToken(token),position,params.loadSize)
    LoadResult.Page(
        data = responseData.body()!!.listStory,
        prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
        nextKey = if (responseData.body()?.listStory?.isEmpty() == true) null else position + 1
    )
        }catch (exception: Exception){
            return LoadResult.Error(exception)
        }
    }

    private fun makeBearerToken(token: String): String {
        return ConstantsMain.TOKEN_BEARER+token
    }
}
