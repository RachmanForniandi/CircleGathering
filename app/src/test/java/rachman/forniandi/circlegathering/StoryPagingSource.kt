package rachman.forniandi.circlegathering

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity

class StoryPagingSource :
    PagingSource<Int, LiveData<List<StoriesEntity>>>() {

        companion object{
            fun snapShotData(items: List<StoriesEntity>): PagingData<StoriesEntity>{
                return PagingData.from(items)
            }
        }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoriesEntity>>>): Int? =0


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoriesEntity>>> =
        LoadResult.Page(emptyList(),0,1)
}