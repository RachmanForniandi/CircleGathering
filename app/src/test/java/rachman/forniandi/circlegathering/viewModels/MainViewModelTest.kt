package rachman.forniandi.circlegathering.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import rachman.forniandi.circlegathering.MainDispatchRule
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.repositories.MainRepository
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import org.mockito.Mock
import org.mockito.Mockito
import rachman.forniandi.circlegathering.StoryDummy
import rachman.forniandi.circlegathering.StoryPagingSource
import rachman.forniandi.circlegathering.adapters.MainNewAdapter

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatchRule = MainDispatchRule()

    @Mock
    private lateinit var mainRepository: MainRepository
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var viewModel: MainViewModel
    private val dummyToken = "dummy_token"


    @Before
    fun setup() {
        mainRepository = mock(MainRepository::class.java)
        dataStoreRepository = mock(DataStoreRepository::class.java)

        `when`(dataStoreRepository.getTheTokenAuth()).thenReturn(flowOf(dummyToken))

        viewModel = MainViewModel(
            mainRepository,
            dataStoreRepository,
            mock(android.app.Application::class.java)
        )
    }

    @Test
    fun `when getAllStoriesPerPages returns success - data is not null and correct`() = runTest {
        val dummyStories = StoryDummy.generateDummyListStoryEntity()

        val pagingData = StoryPagingSource.snapShotData(dummyStories)

        val expectedStories = MutableLiveData<PagingData<StoriesEntity>>()

        expectedStories.value = pagingData

        `when`(mainRepository.getAllStoriesPerPage(dummyToken)).thenReturn(flowOf(pagingData))

        val result = viewModel.getAllStoriesPerPages().first()


        val differ = AsyncPagingDataDiffer(
            diffCallback = MainNewAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatchRule.testDispatcher,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(result)

        //advanceUntilIdle()

        Mockito.verify(viewModel).getAllStoriesPerPages()
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when getAllStoriesPerPages returns empty - list size is zero`() = runTest {
        val emptyData = PagingData.from(emptyList<StoriesEntity>())

        `when`(mainRepository.getAllStoriesPerPage(dummyToken)).thenReturn(flowOf(emptyData))

        val result = viewModel.getAllStoriesPerPages().first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<StoriesEntity>() {
                override fun areItemsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
                    return oldItem == newItem
                }
            },
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatchRule.testDispatcher,
            workerDispatcher = mainDispatchRule.testDispatcher
        )

        differ.submitData(result)

        advanceUntilIdle()

        assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
