package rachman.forniandi.circlegathering.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import rachman.forniandi.circlegathering.utils.ConstantsMain.Companion.PREFERENCES_BACK_ONLINE
import rachman.forniandi.circlegathering.utils.ConstantsMain.Companion.PREFERENCES_NAME
import javax.inject.Inject


private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

@ActivityRetainedScoped
class KeyOnlineSource @Inject constructor(@ApplicationContext private val context: Context){

    private object PreferenceKeys{
        val backOnline = booleanPreferencesKey(PREFERENCES_BACK_ONLINE)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveBackOnline(backOnline:Boolean){
        dataStore.edit { preferences->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }
}