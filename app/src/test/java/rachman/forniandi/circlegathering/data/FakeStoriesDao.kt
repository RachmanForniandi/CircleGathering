package rachman.forniandi.circlegathering.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import rachman.forniandi.circlegathering.dBRoom.StoriesDao
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity

class FakeStoriesDao : StoriesDao {
    private val storyData = mutableListOf<StoriesEntity>()

    override suspend fun insertStories(story: List<StoriesEntity>) {
        storyData.addAll(story)
    }

    override fun readAllStories(): PagingSource<Int, StoriesEntity> {
        return object : PagingSource<Int, StoriesEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoriesEntity> {
                return LoadResult.Page(
                    data = storyData,
                    prevKey = null,
                    nextKey = null
                )
            }
            override fun getRefreshKey(state: PagingState<Int, StoriesEntity>): Int? {
                return null
            }
        }

    }

    override suspend fun deleteAllStories() {
        storyData.clear()
    }


}