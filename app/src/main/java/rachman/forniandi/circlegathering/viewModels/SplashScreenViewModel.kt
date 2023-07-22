package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.SessionPreferences
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val sessionPreferences: DataStoreRepository,
                                                application: Application) : AndroidViewModel(application) {

        fun checkUserStatus() = sessionPreferences.getLoginUserStatus().asLiveData()
}