package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.repositories.AuthUserRepository
import rachman.forniandi.circlegathering.utils.SessionPreferences
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val sessionPreferences: SessionPreferences,
                                                application: Application
) :
    AndroidViewModel(application) {

        fun checkUserStatus() = sessionPreferences.getLoginUserStatus().asLiveData()
}