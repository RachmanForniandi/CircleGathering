package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.repositories.MainRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.SessionPreferences
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repository: MainRepository,
    private val sessionPreferences: SessionPreferences,
    application: Application
): AndroidViewModel(application) {

    var inputDataResponse: MutableLiveData<NetworkResult<ResponseAddStory>> = MutableLiveData()

    fun doUploadStoriesData(description: RequestBody, filePicture: MultipartBody.Part)= viewModelScope.launch {
        actionSafeCallUploadStories(description,filePicture)
    }


    private suspend fun actionSafeCallUploadStories(description: RequestBody,
                                                    filePicture: MultipartBody.Part) {
        inputDataResponse.value = NetworkResult.Loading()
        if(hasInternetConnectionForMain()){
            try {
                val tokenForUpload= sessionPreferences.getTheTokenAuth().first()
                val uploadDataFeedback = repository.remoteMain.addDataStories(tokenForUpload,description,filePicture)
                inputDataResponse.value  = handledUploadDataStoriesResponse(uploadDataFeedback)
            }catch (e: Exception){
                inputDataResponse.value  = NetworkResult.Error("Data not Available.")
            }
        }
    }

    private fun handledUploadDataStoriesResponse(response: Response<ResponseAddStory>): NetworkResult<ResponseAddStory>? {
        when{
            response.message().toString().contains("timeout")->{
                return NetworkResult.Error("Timeout")
            }
            response.isSuccessful -> {
                val dataStories = response.body()
                return NetworkResult.Success(dataStories )
            }
            else->{
                return NetworkResult.Error(response.message())
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