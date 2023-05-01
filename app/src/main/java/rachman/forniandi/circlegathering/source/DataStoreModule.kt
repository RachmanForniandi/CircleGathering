package rachman.forniandi.circlegathering.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rachman.forniandi.circlegathering.utils.TokenDataSource
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application_circle")

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    fun featureDataStore(@ApplicationContext context: Context): DataStore<Preferences>{
        return context.dataStore
    }

    @Provides
    @Singleton
    fun forAuthFeaturePreferences(dataStore: DataStore<Preferences>):TokenDataSource{
        return TokenDataSource(dataStore)
    }

}