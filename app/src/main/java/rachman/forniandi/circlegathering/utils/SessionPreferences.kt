package rachman.forniandi.circlegathering.utils

import kotlinx.coroutines.flow.Flow

interface SessionPreferences {

    suspend fun saveTokenAuth(token: String)

    suspend fun deleteTokenAuth()

    fun getTheTokenAuth(): Flow<String>

    suspend fun saveUsername(name: String)

    fun getUsername(): Flow<String>

    suspend fun deleteUsername()

    suspend fun setLoginUserStatus(isLogin: Boolean)

    fun getLoginUserStatus(): Flow<Boolean>

    suspend fun saveBackOnline(backOnline:Boolean)
}