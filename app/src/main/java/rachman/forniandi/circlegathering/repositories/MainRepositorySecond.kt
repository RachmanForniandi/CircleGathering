package rachman.forniandi.circlegathering.repositories

import androidx.room.withTransaction
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import rachman.forniandi.circlegathering.DBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.networkUtil.NetworkService
import rachman.forniandi.circlegathering.source.LocalDataSource
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.NetworkHelper
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.networkBoundResource
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class MainRepositorySecond @Inject constructor(
    private val database: StoriesDatabase,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dataStoreRepository: DataStoreRepository,
    private val networkHelper: NetworkHelper
){
    val daoStories = database.storiesDao()
    //private var responseOfStories=  runBlocking { remoteDataSource.showStories(dataStoreRepository.getTheTokenAuth().first()) }
    private var convertResponseToEntity:StoriesEntity?=null

    fun getDataStories() = networkBoundResource(
        // pass in the logic to query data from the database
        query = {
            //daoStories.readStories()
                localDataSource.readDbStories()
        },
        // pass in the logic to fetch data from the api
        fetch = {
            delay(2000)
            runBlocking { remoteDataSource.showStories(dataStoreRepository.getTheTokenAuth().first()) }
            /*val allStories =handledAllStoriesResponse(responseOfStories)?.data
            if (allStories != null){
                offlineCacheStories(allStories)
            }*/

            dataStoreRepository.getUsername()
        },

        //pass in the logic to save the result to the local cache
        saveFetchResult = {
            database.withTransaction {
                convertResponseToEntity?.let { entityStories -> localDataSource.insertStories(entityStories) }
            }
        },

        //pass in the logic to determine if the networking call should be made
        shouldFetch = {
            networkHelper.hasInternetConnectionForMain()
        }
    )

    private suspend fun offlineCacheStories(allStories: ResponseAllStories) {
        val storiesEntity = StoriesEntity(allStories)
        insertDataStoriesLocalDb(storiesEntity)
    }

    private suspend fun insertDataStoriesLocalDb(storiesEntity: StoriesEntity) {
        daoStories.insertStories(storiesEntity)
    }


    private fun handledAllStoriesResponse(response: Response<ResponseAllStories>): NetworkResult<ResponseAllStories>? {
        return when{
            response.message().toString().contains("timeout")->{
                NetworkResult.Error("Timeout")
            }

            response.body()!!.listStory.isEmpty()->{
                val storiesData = response.body()
                return NetworkResult.Success(storiesData)
            }
            response.isSuccessful -> {
                val dataStories = response.body()
                return NetworkResult.Success(dataStories)
            }

            else->{
                NetworkResult.Error(response.message())
            }
        }
    }
}
