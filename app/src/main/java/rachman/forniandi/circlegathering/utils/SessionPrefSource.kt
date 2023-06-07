package rachman.forniandi.circlegathering.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionPrefSource @Inject
constructor(private val prefData: DataStore<Preferences>):SessionPreferences{

    companion object{
        private val KEY_TOKEN_STORE = stringPreferencesKey("token_stories")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_USER_LOGIN_STATUS = booleanPreferencesKey("user_login_status")

    }

    fun checkAuthToken(): Flow<String?> {
        return prefData.data.map { pref ->
            pref[KEY_TOKEN_STORE]
        }
    }

    override suspend fun saveTokenAuth(token: String) {
        prefData.edit { pref->
            pref[KEY_TOKEN_STORE] = token
        }
    }

    override suspend fun deleteTokenAuth() {
        prefData.edit {
            it.remove(KEY_TOKEN_STORE)
        }
    }

    override fun getTheTokenAuth()=
        prefData.data.map {
            it[KEY_TOKEN_STORE]?: ""
        }

    override suspend fun saveUsername(name:String){
        prefData.edit { pref ->
            pref[KEY_USERNAME] = name
        }
    }

    override fun getUsername()=
        prefData.data.map {
            it[KEY_USERNAME]?: ""
        }

    override suspend fun deleteUsername() {
        prefData.edit {
            it.remove(KEY_TOKEN_STORE)
        }
    }

    override suspend fun setLoginUserStatus(isLogin: Boolean) {
        prefData.edit {
            it[KEY_USER_LOGIN_STATUS] = isLogin
        }
    }

    override fun getLoginUserStatus()=
        prefData.data.map {
            it[KEY_USER_LOGIN_STATUS]?: false
        }



}