package rachman.forniandi.circlegathering.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import rachman.forniandi.circlegathering.utils.ConstantsMain
import javax.inject.Inject

/*
private val Context.dataStore by preferencesDataStore(ConstantsMain.PREFERENCES_NAME)
@ActivityRetainedScoped
class KeyOnlineSource @Inject constructor(@ApplicationContext private val context: Context){

    private object PreferenceKeys{
        val backOnline = booleanPreferencesKey(ConstantsMain.PREFERENCES_BACK_ONLINE)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveBackOnline(backOnline:Boolean){
        dataStore.edit { preferences->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }
}*/
