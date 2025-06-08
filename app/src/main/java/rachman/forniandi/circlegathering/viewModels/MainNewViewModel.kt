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
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.repositories.MainNewRepository
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.addedBearerToToken
import retrofit2.Response
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MainNewViewModel @Inject constructor(
    private val repository: MainNewRepository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    var networkStatus = false
    var backOnline = false

    var recyclerViewState: Parcelable? = null

    // LiveData: nama user
    fun getUserName() = dataStoreRepository.getUsername().asLiveData()

    // DataStore: status koneksi sebelumnya
    var readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    // Cache token dan paging flow
    private var authToken: String? = null
    private var _pagingFlow: Flow<PagingData<StoriesEntity>>? = null

    fun getAllStoriesPerPages(): Flow<PagingData<StoriesEntity>> {
        return _pagingFlow ?: flow {
            if (authToken == null) {
                authToken = dataStoreRepository.getTheTokenAuth().first()
            }
            val newFlow = repository.getAllStoriesPerPage(authToken ?: "").cachedIn(viewModelScope)
            _pagingFlow = newFlow
            emitAll(newFlow)
        }
    }

    // Reset paging data (jika logout / force refresh)
    fun resetPagingFlow() {
        _pagingFlow = null
    }

    // Logout user
    fun signOutUser() = viewModelScope.launch {
        dataStoreRepository.run {
            deleteTokenAuth()
            setLoginUserStatus(false)
        }
        resetPagingFlow()
    }

    // Simpan status back online
    private fun saveBackOnline(backOnline: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.saveBackOnline(backOnline)
    }

    // Tampilkan status koneksi
    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus && backOnline) {
            Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
            saveBackOnline(false)
        }
    }

    // Cek koneksi internet
    private fun hasInternetConnectionForMain(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}