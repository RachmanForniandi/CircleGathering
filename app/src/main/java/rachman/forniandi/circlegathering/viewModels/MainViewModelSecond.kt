package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.repositories.MainRepositorySecond
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModelSecond @Inject constructor(
    private val repository: MainRepositorySecond,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
): AndroidViewModel(application) {

    var networkStatus = false
    var backOnline = false

    var recyclerViewState: Parcelable? = null

    var getAllStoriesResponse: MutableLiveData<NetworkResult<ResponseAllStories>> = MutableLiveData()
    var readBackOnline = dataStoreRepository.readBackOnline.asLiveData()
    val readStoriesLocal: LiveData<List<StoriesEntity>> = repository.daoStories.readStories().asLiveData()


    fun getUserName()= dataStoreRepository.getUsername().asLiveData()
    //val doShowAllStoriesData= repository.getDataStories().asLiveData()

    fun doShowAllStoriesData()= viewModelScope.launch {
        actionSafeCallShowAllStories()
    }

    private suspend fun actionSafeCallShowAllStories() {
        getAllStoriesResponse.value = NetworkResult.Loading()
        if(hasInternetConnectionForMain()){
            try {
                //val tokenAuth= dataStoreRepository.getTheTokenAuth().first()
                repository.getDataStories().asLiveData()
                /*getAllStoriesResponse.value =handledAllStoriesResponse()
                Log.e("check_feedback",""+storiesFeedback)*/

                /*val allStories = getAllStoriesResponse.value?.data
                Log.e("check_story",""+allStories)*/
                /*if (allStories != null){
                    offlineCacheStories(allStories)
                }*/
            }catch (e: Exception){
                getAllStoriesResponse.value  = NetworkResult.Error("Data not Available.")
            }
        }else{
            getAllStoriesResponse.value  = NetworkResult.Error("No Internet Connection.")
        }
    }


    //nanti gak dipakai lg fungsi ini
    /*private fun handledAllStoriesResponse(response: Response<ResponseAllStories>): NetworkResult<ResponseAllStories>? {
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
    }*/
    fun signOutUser() = viewModelScope.launch {
        dataStoreRepository.run {
            deleteTokenAuth()
            setLoginUserStatus(false)
        }
    }

    private fun saveBackOnline(backOnline:Boolean)=
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun showNetworkStatus(){
        if (!networkStatus){
            Toast.makeText(getApplication(),"No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        }else if (networkStatus){
            if (backOnline){
                Toast.makeText(getApplication(),"We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
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