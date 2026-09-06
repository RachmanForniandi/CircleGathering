package rachman.forniandi.circlegathering.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import javax.inject.Inject

@HiltViewModel
class AboutUserViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel(){

    fun getUserName() = dataStoreRepository.getUsername().asLiveData()
    fun getUserId() = dataStoreRepository.getUserId().asLiveData()

    // Theme (Day / Night)
    fun getTheme() = dataStoreRepository.getTheme().asLiveData()

    fun saveTheme(isDarkMode: Boolean) = viewModelScope.launch {
        dataStoreRepository.setTheme(isDarkMode)
    }

    fun signOutUser() = viewModelScope.launch {
        dataStoreRepository.run {
            deleteTokenAuth()
            setLoginUserStatus(false)
        }
    }
}