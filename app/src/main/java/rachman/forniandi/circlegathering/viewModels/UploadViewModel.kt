package rachman.forniandi.circlegathering.viewModels


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.repositories.MainRepository
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repository: MainRepository,
    private val sessionPreferences: DataStoreRepository
): ViewModel() {

    var inputDataResponse: MutableLiveData<NetworkResult<ResponseAddStory>> = MutableLiveData()


    fun doUploadStoriesData(imgStory: MultipartBody. Part,
                            description: RequestBody,
                            lat: RequestBody?,
                            lon: RequestBody?)= viewModelScope.launch {
        actionSafeCallUploadStories(imgStory, description,lat, lon)
    }



    private suspend fun actionSafeCallUploadStories(filePicture: MultipartBody.Part,
                                                    description: RequestBody,
                                                    lat: RequestBody?,
                                                    lon: RequestBody?
    ) {
        inputDataResponse.value = NetworkResult.Loading()
        try {
            val tokenForUpload= sessionPreferences.getTheTokenAuth().first()
            val uploadDataFeedback = repository.remoteMain.addDataStories(tokenForUpload,filePicture,description,lat, lon)
            Log.e("check_token_upload_bearer",""+tokenForUpload)
            Log.e("check_file_picture","parameter_file:"+ filePicture)
            Log.e("check_description","parameter_description:"+ description)
            Log.e("check_lat","parameter_lat:"+ lat)
            Log.e("check_lon","parameter_lon:"+ lon)
            inputDataResponse.value  = handledUploadDataStoriesResponse(uploadDataFeedback)
        }catch (e: Exception){
            inputDataResponse.value  = NetworkResult.Error("Upload Error")
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

}