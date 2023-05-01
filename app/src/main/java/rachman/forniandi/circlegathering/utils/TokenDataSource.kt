package rachman.forniandi.circlegathering.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenDataSource @Inject
constructor(private val prefData: DataStore<Preferences>){

    companion object{
        private val KEY_TOKEN_STORE = stringPreferencesKey("token_stories")
        private val KEY_USERNAME = stringPreferencesKey("username")
    }

    fun obtainAuthToken(): Flow<String?> {
        return prefData.data.map { pref ->
            pref[KEY_TOKEN_STORE]
        }
    }

    suspend fun saveTheAuthToken(token:String){
        prefData.edit { pref ->
            pref[KEY_TOKEN_STORE] = token
        }
    }

    suspend fun saveUsername(name:String){
        prefData.edit { pref ->
            pref[KEY_USERNAME] = name
        }
    }

    suspend fun clearUsername(){
        prefData.edit {
            it.clear()
        }
    }

}