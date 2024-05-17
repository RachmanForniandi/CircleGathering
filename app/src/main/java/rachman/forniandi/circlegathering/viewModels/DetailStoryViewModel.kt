package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.models.detailStories.ResponseDetailStory
import rachman.forniandi.circlegathering.repositories.MainRepository
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class DetailStoryViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
): AndroidViewModel(application) {

    private var getDetailStoriesResponse: MutableLiveData<NetworkResult<ResponseDetailStory>> = MutableLiveData()

    fun doShowAllStoriesData(iDStory:String)= viewModelScope.launch {
        actionSafeCallShowDetailStories(iDStory)
    }

    private suspend fun actionSafeCallShowDetailStories(iDStory:String) {
        getDetailStoriesResponse.value = NetworkResult.Loading()
        if(hasInternetConnectionForMain()){
            try {
                val tokenAuth= dataStoreRepository.getTheTokenAuth().first()
                val storiesFeedback = repository.remoteMain.showDetailStories(tokenAuth,iDStory)
                Log.e("check_token_auth",""+tokenAuth)
                getDetailStoriesResponse.value  = handledDetailStoriesResponse(storiesFeedback)

                val allStories = getDetailStoriesResponse.value?.data
                Log.e("check_story",""+allStories)
               
            }catch (e: Exception){
                getDetailStoriesResponse.value  = NetworkResult.Error("Data not Available.")
            }
        }else{
            getDetailStoriesResponse.value  = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handledDetailStoriesResponse(response: Response<ResponseDetailStory>): NetworkResult<ResponseDetailStory>? {
        return when{
            response.message().toString().contains("timeout")->{
                NetworkResult.Error("Timeout")
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
    private fun hasInternetConnectionForMain():Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        )as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities= connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
            else -> false
        }
    }
}