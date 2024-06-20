package rachman.forniandi.circlegathering.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import rachman.forniandi.circlegathering.utils.ConstantsMain.Companion.PREFERENCES_BACK_ONLINE
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DataStoreRepository @Inject
constructor(private val prefData: DataStore<Preferences>):SessionPreferences{


    override suspend fun saveBackOnline(backOnline:Boolean){
        prefData.edit { preferences->
            preferences[BACK_ONLINE] = backOnline
        }
    }

    val readBackOnline: Flow<Boolean> = prefData.data
        .catch { exception->
            if (exception is IOException){
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }.map { preferences ->
            val backOnline= preferences[BACK_ONLINE] ?:false
            backOnline
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


    companion object{
        val KEY_TOKEN_STORE = stringPreferencesKey("token_stories")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_USER_LOGIN_STATUS = booleanPreferencesKey("user_login_status")
        val BACK_ONLINE = booleanPreferencesKey(PREFERENCES_BACK_ONLINE)

    }
}