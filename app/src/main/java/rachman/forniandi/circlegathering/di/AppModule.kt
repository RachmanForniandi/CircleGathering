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
import rachman.forniandi.circlegathering.stackWidgets.SourceForWidgetStoryUseCase
import rachman.forniandi.circlegathering.stackWidgets.WidgetDataActuator
import rachman.forniandi.circlegathering.utils.ConstantsMain.Companion.PREFERENCES_NAME
import rachman.forniandi.circlegathering.utils.SessionPrefSource
import rachman.forniandi.circlegathering.utils.SessionPreferences
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideAuthUserFeaturePreferences(sessionPreferences: SessionPrefSource): SessionPreferences


    @Binds
    @Singleton
    abstract fun provideFeatureWidgetStory(widgetDataActuator: WidgetDataActuator): SourceForWidgetStoryUseCase

    companion object{
        @Provides
        @Singleton
        fun providePrefDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.dataStore
        }
    }

}