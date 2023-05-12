package rachman.forniandi.circlegathering.source

import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.utils.SessionPrefSource
import javax.inject.Inject

class SessionDataSource @Inject constructor(
    private val tokenDataSource: SessionPrefSource
){

    suspend fun keepAuthToken(token: String) {
        tokenDataSource.saveTheAuthToken(token)
    }

    fun checkToken(): Flow<String?> =
        tokenDataSource.checkAuthToken()


    suspend fun keepDataUsername(name: String) {
        tokenDataSource.saveUsername(name)
    }

    suspend fun clearUsername() {
        tokenDataSource.clearUsername()
    }

}