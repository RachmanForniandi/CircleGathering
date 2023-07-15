package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.ListStoryItem
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.repositories.MainRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.SessionPreferences
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository:MainRepository,
    private val sessionPreferences: SessionPreferences,
    application: Application
): AndroidViewModel(application) {

    var networkStatus = false
    var backOnline = false

    var getAllStoriesResponse: MutableLiveData<NetworkResult<ResponseAllStories>> = MutableLiveData()
    fun getUserName()= sessionPreferences.getUsername().asLiveData()

    val readStoriesLocal:LiveData<List<StoriesEntity>> = repository.localMain.readDbStories().asLiveData()

    fun saveDataStories(storiesEntity: StoriesEntity)=
        viewModelScope.launch(Dispatchers.IO) {
            repository.localMain.insertStories(storiesEntity)
        }


    fun signOutUser() = viewModelScope.launch {
        sessionPreferences.run {
            deleteTokenAuth()
            setLoginUserStatus(false)
        }
    }

    fun saveBackOnline(backOnline:Boolean)=
        viewModelScope.launch(Dispatchers.IO) {
            sessionPreferences.saveBackOnline(backOnline)
        }
    /*fun getUserLoginStatus()= sessionPreferences.getLoginUserStatus().asLiveData()
*/

    fun doShowAllStoriesData()= viewModelScope.launch {
        actionSafeCallShowAllStories()
    }

    private suspend fun actionSafeCallShowAllStories() {
        getAllStoriesResponse.value = NetworkResult.Loading()
        if(hasInternetConnectionForMain()){
            try {
                val tokenAuth= sessionPreferences.getTheTokenAuth().first()
                val storiesFeedback = repository.remoteMain.showStories(tokenAuth)
                Log.e("check_token_auth",""+tokenAuth)
                getAllStoriesResponse.value  = handledAllStoriesResponse(storiesFeedback)
                val allStories = getAllStoriesResponse.value?.data
                if (allStories != null){
                    offlineCacheStories(allStories)
                }
            }catch (e: Exception){
                getAllStoriesResponse.value  = NetworkResult.Error("Data not Available.")
            }
        }
    }

    private fun offlineCacheStories(allStories: ResponseAllStories) {
        val storiesEntity = StoriesEntity(allStories as ListStoryItem)
        saveDataStories(storiesEntity)
    }

    private fun handledAllStoriesResponse(response: Response<ResponseAllStories>): NetworkResult<ResponseAllStories>? {
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