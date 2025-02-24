package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.models.addStory.InputStoryRequest
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.repositories.MainRepository
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import retrofit2.Response
import java.io.File
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repository: MainRepository,
    private val sessionPreferences: DataStoreRepository,
    application: Application
): AndroidViewModel(application) {

    var inputDataResponse: MutableLiveData<NetworkResult<ResponseAddStory>> = MutableLiveData()

    fun doUploadStoriesData(imgStory: File,
                            descriptionStory: String,
                            location: Location? = null )= viewModelScope.launch {
        actionSafeCallUploadStories(imgStory, descriptionStory, location)
    }

    private suspend fun actionSafeCallUploadStories(imgStory: File,
                                                    descriptionStory: String,
                                                    location: Location? = null) {
        inputDataResponse.value = NetworkResult.Loading()
        if(hasInternetConnectionForMain()){
            try {
                val tokenForUpload= sessionPreferences.getTheTokenAuth().first()
                val inputDataRequest = InputStoryRequest(imgStory, descriptionStory, location)
                //val convertTokenToRequestBody = tokenForUpload.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                //Log.e("check_token_upload",""+tokenForUpload)
                val uploadDataFeedback = repository.remoteMain.addDataStories(tokenForUpload,inputDataRequest)
                Log.e("check_token_upload_bearer",""+tokenForUpload)
                Log.e("check_request_data_input","parameter_content:"+ uploadDataFeedback)
                inputDataResponse.value  = handledUploadDataStoriesResponse(uploadDataFeedback)
            }catch (e: Exception){
                inputDataResponse.value  = NetworkResult.Error("Upload Error")
            }

            Log.e("response_value:",""+inputDataResponse.value)
        }
    }

    private fun handledUploadDataStoriesResponse(response: Response<ResponseAddStory>): NetworkResult<ResponseAddStory> {
        return when{
            response.message().toString().contains("timeout")->{
                NetworkResult.Error("Timeout")
            }
            response.isSuccessful -> {
                val dataStories = response.body()
                NetworkResult.Success(dataStories )
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