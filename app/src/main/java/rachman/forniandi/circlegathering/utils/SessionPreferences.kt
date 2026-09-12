package rachman.forniandi.circlegathering.utils

import kotlinx.coroutines.flow.Flow

interface SessionPreferences {

    suspend fun saveTokenAuth(token: String)

    suspend fun deleteTokenAuth()

    fun getTheTokenAuth(): Flow<String>

    suspend fun saveUsername(name: String)

    suspend fun saveUserId(userId: String)

    fun getUsername(): Flow<String>

    fun getUserId(): Flow<String>

    suspend fun deleteUsername()

    suspend fun setLoginUserStatus(isLogin: Boolean)

    fun getLoginUserStatus(): Flow<Boolean>

    fun getTheme(): Flow<Boolean>

    suspend fun setTheme(isDarkModeThemeActive:Boolean)

    suspend fun saveBackOnline(backOnline:Boolean)
}