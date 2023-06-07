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
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.repositories.AuthUserRepository
import rachman.forniandi.circlegathering.repositories.MainRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.SessionPreferences
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository1: AuthUserRepository,
    private val repository2:MainRepository,
    private val sessionPreferences: SessionPreferences,
    application: Application
): AndroidViewModel(application) {

    var getAllStoriesResponse: MutableLiveData<NetworkResult<ResponseAllStories>> = MutableLiveData()
    var inputDataResponse: MutableLiveData<NetworkResult<ResponseAddStory>> = MutableLiveData()


    fun clearTheTokenAndSession(tokenStory:String){
        viewModelScope.launch {
            repository1.store.keepAuthToken(tokenStory)
        }
    }

    fun getUserName()= sessionPreferences.getUsername()

    /*suspend fun signOutUser(){
        sessionPreferences.run {
            deleteTokenAuth()
            setLoginUserStatus(false)
        }
    }*/

    fun signOutUser() = viewModelScope.launch {
        sessionPreferences.run {
            deleteTokenAuth()
            setLoginUserStatus(false)
        }
    }

    fun getUserLoginStatus()= sessionPreferences.getLoginUserStatus()

    /*fun actionClearDataUserName(){
        viewModelScope.launch {
            repository1.store.clearUsername()
        }
    }*/

    fun doShowAllStoriesData()= viewModelScope.launch {
        actionSafeCallShowAllStories()
    }

    private suspend fun actionSafeCallShowAllStories() {
        getAllStoriesResponse.value = NetworkResult.Loading()
        if(hasInternetConnectionForMain()){
            try {
                val tokenAuth= sessionPreferences.getTheTokenAuth().first()
                val storiesFeedback = repository2.remoteMain.showStories(tokenAuth)
                getAllStoriesResponse.value  = handledAllStoriesResponse(storiesFeedback)
            }catch (e: Exception){
                getAllStoriesResponse.value  = NetworkResult.Error("Data not Available.")
            }
        }
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