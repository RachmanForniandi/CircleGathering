package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Parcelable
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.paging.StoryPagingResource
import rachman.forniandi.circlegathering.repositories.MainNewRepository
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MainNewViewModel @Inject constructor(
    private val repository: MainNewRepository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
): AndroidViewModel(application) {

    var networkStatus = false
    var backOnline = false

    var recyclerViewState: Parcelable? = null

    private val refreshTrigger = MutableLiveData<Unit>()

    //datastore get variable name
    fun getUserName()= dataStoreRepository.getUsername().asLiveData()

    //room
    var readBackOnline = dataStoreRepository.readBackOnline.asLiveData()


    fun getAllStoriesPerPages(): LiveData<PagingData<StoriesEntity>>{
        val token = dataStoreRepository.getTheTokenAuth().toString()
        return repository.getAllStoriesPerPage(token).cachedIn(viewModelScope).asLiveData()
    }


    //logout & clear token dari data store
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