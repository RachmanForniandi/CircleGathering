package rachman.forniandi.circlegathering.repositories

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import rachman.forniandi.circlegathering.MainDispatchRule
import rachman.forniandi.circlegathering.StoryDummy
import rachman.forniandi.circlegathering.StoryPagingSource
import rachman.forniandi.circlegathering.adapters.MainNewAdapter
import rachman.forniandi.circlegathering.dBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.data.FakeStoriesDao
import rachman.forniandi.circlegathering.source.RemoteDataSource
import retrofit2.Response

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatchRule()

    @Mock
    private lateinit var remoteDataSource: RemoteDataSource

    @Mock
    private lateinit var database: StoriesDatabase

    private lateinit var mainRepository: MainRepository

    private val dummyToken = "auth_token"

    @Before
    fun setup() {
        val fakeDao = FakeStoriesDao()
        `when`(database.storiesDao()).thenReturn(fakeDao)

        // Simulasi data disimpan oleh RemoteMediator → insertStories()
        val dummyData = StoryDummy.generateDummyListStoryEntity()
        runBlocking {
            fakeDao.insertStories(dummyData)
        }

        mainRepository = MainRepository(database, remoteDataSource)
    }

    @Test
    fun `getAllStoriesPerPage should return correct PagingData`() = runTest {
        val dummyData = StoryDummy.generateDummyListStoryEntity()

        val flowResult = mainRepository.getAllStoriesPerPage(dummyToken)

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainNewAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRule.testDispatcher,
            workerDispatcher = mainDispatcherRule.testDispatcher
        )

        val job = launch {
            flowResult.collectLatest {
                differ.submitData(it)
            }
        }

        advanceUntilIdle() // Menunggu semua submit selesai
        job.cancel()     // biar paging selesai

        assertNotNull(differ.snapshot())
        assertEquals(dummyData.size, differ.snapshot().size)
        assertEquals(dummyData[0], differ.snapshot()[0])
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
