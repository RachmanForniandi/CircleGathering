package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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

    private val _uploadResponse = MutableLiveData<NetworkResult<ResponseAddStory>>()
    val uploadResponseLiveData:LiveData<NetworkResult<ResponseAddStory>>
        get() = _uploadResponse



    var inputDataResponse: MutableLiveData<NetworkResult<ResponseAddStory>> = MutableLiveData()

    fun doUploadStoriesData(filePicture: MultipartBody.Part,description: RequestBody )= viewModelScope.launch {
        actionSafeCallUploadStories(filePicture,description)
    }

    private suspend fun actionSafeCallUploadStories(filePicture: MultipartBody.Part,
                                                    description: RequestBody) {
        inputDataResponse.value = NetworkResult.Loading()
        if(hasInternetConnectionForMain()){
            try {
                val tokenForUpload= sessionPreferences.getTheTokenAuth().first()
                //val convertTokenToRequestBody = tokenForUpload.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                //Log.e("check_token_upload",""+tokenForUpload)
                val uploadDataFeedback = repository.remoteMain.addDataStories(tokenForUpload,filePicture,description)
                Log.e("check_token_upload_bearer",""+tokenForUpload)
                Log.e("check_file_picture","parameter_file:"+ filePicture)
                Log.e("check_description","parameter_description:"+ description)
                inputDataResponse.value  = handledUploadDataStoriesResponse(uploadDataFeedback)
            }catch (e: Exception){
                //inputDataResponse.value  = NetworkResult.Error("Upload Error")
                _uploadResponse.postValue(NetworkResult.Error("Upload Error"))
            }

            Log.e("response_value:",""+inputDataResponse.value)
        }
    }

    private fun handledUploadDataStoriesResponse(response: Response<ResponseAddStory>): NetworkResult<ResponseAddStory> {
        return when{
            response.message().toString().contains("timeout")->{
                _uploadResponse.postValue(NetworkResult.Error("Timeout"))
                NetworkResult.Error("Timeout")
            }
            response.isSuccessful -> {
                val dataStories = response.body()
                _uploadResponse.postValue(NetworkResult.Success(dataStories))
                NetworkResult.Success(dataStories)
            }
            else->{
                _uploadResponse.postValue(NetworkResult.Error(response.message()))
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