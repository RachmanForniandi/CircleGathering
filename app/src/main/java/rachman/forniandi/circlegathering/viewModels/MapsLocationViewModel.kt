package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.repositories.MainRepository
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MapsLocationViewModel @OptIn(ExperimentalPagingApi::class)
@Inject constructor(
    private val repository: MainRepository,
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    var getAllStoriesLocationResponse: MutableLiveData<NetworkResult<ResponseAllStories>> = MutableLiveData()


    fun doShowAllStoriesLocationData()= viewModelScope.launch {
        actionSafeCallShowAllStoriesAndLocation()
    }

    @OptIn(ExperimentalPagingApi::class)
    private suspend fun actionSafeCallShowAllStoriesAndLocation() {
        getAllStoriesLocationResponse.value = NetworkResult.Loading()
        try {
            val tokenAuth= dataStoreRepository.getTheTokenAuth().first()
            val storiesFeedback = repository.remoteMain.showStoriesWithLocation(tokenAuth)
            Log.e("check_token_auth",""+tokenAuth)
            getAllStoriesLocationResponse.value  = handledAllStoriesAndLocationResponse(storiesFeedback)

            val allStories = getAllStoriesLocationResponse.value?.data
            Log.e("check_story",""+allStories)

        }catch (e: Exception){
            getAllStoriesLocationResponse.value  = NetworkResult.Error("Data not Available.")
        }
    }

    private fun handledAllStoriesAndLocationResponse(response: Response<ResponseAllStories>): NetworkResult<ResponseAllStories>? {
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