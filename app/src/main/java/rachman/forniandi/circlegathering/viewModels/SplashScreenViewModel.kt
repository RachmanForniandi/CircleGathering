package rachman.forniandi.circlegathering.viewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.repositories.AuthUserRepository
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val repository: AuthUserRepository) :
    ViewModel() {

        fun checkSessionToken(): Flow<String?> = repository.store.checkToken()
}