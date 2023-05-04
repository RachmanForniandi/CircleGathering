package rachman.forniandi.circlegathering.source

import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.utils.TokenDataSource
import javax.inject.Inject

class SessionDataSource @Inject constructor(
    private val tokenDataSource: TokenDataSource
){


    suspend fun keepAuthToken(token: String) {
        tokenDataSource.saveTheAuthToken(token)
    }

    fun checkToken(): Flow<String?> =
        tokenDataSource.obtainAuthToken()


    suspend fun keepDataUsername(name: String) {
        tokenDataSource.saveUsername(name)
    }

    suspend fun clearUsername() {
        tokenDataSource.clearUsername()
    }

}