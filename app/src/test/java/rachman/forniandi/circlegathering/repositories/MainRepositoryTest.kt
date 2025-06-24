package rachman.forniandi.circlegathering.repositories

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import rachman.forniandi.circlegathering.MainDispatchRule
import rachman.forniandi.circlegathering.StoryDummy
import rachman.forniandi.circlegathering.StoryPagingSource
import rachman.forniandi.circlegathering.adapters.MainNewAdapter
import rachman.forniandi.circlegathering.dBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.source.RemoteDataSource

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainRepositoryTest {

    @get:Rule
    val mainDispatchRule = MainDispatchRule()

    @Mock
    private lateinit var remoteDataSource: RemoteDataSource

    @Mock
    private lateinit var database: StoriesDatabase

    @Mock
    private lateinit var mainRepository: MainRepository
    private val dummyToken = "authentication_token"
    private val dummyStoryResponse = StoryDummy.generateDummyResponseStories()


    @Before
    fun setup(){
        mainRepository = MainRepository(database,remoteDataSource)
    }

    @Test
    fun getAllStoriesPerPage()= runTest {
        val dummyStoryEntity = StoryDummy.generateDummyListStoryEntity()
        val data = StoryPagingSource.snapShotData(dummyStoryEntity)

        val expectedResult = flowOf(data)

        `when`(mainRepository.getAllStoriesPerPage(dummyToken)).thenReturn(expectedResult)

        mainRepository.getAllStoriesPerPage(dummyToken).collect { realResult->
            val differ = AsyncPagingDataDiffer(
                diffCallback = MainNewAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = mainDispatchRule.testDispatcher,
                workerDispatcher = mainDispatchRule.testDispatcher

            )

            differ.submitData(realResult)

            Assert.assertNull(differ.snapshot())
            Assert.assertEquals(dummyStoryResponse.listStory.size,differ.snapshot().size)
            Assert.assertEquals(dummyStoryEntity[0],differ.snapshot()[0])
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}