package rachman.forniandi.circlegathering.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rachman.forniandi.circlegathering.utils.ConstantsMain.Companion.PREFERENCES_NAME
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import rachman.forniandi.circlegathering.utils.SessionPreferences
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun providePrefDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideAuthUserFeaturePreferences(dataStore: DataStore<Preferences>): DataStoreRepository=
        DataStoreRepository(dataStore)

    /*@Binds
    @Singleton
    abstract fun provideFeatureWidgetStory(widgetDataActuator: WidgetDataActuator): SourceForWidgetStoryUseCase
*/


}