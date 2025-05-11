package rachman.forniandi.circlegathering.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rachman.forniandi.circlegathering.dBRoom.StoriesDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StoriesDatabase=
        Room.databaseBuilder(context,
            StoriesDatabase::class.java,"stories_database").fallbackToDestructiveMigration().build()


    @Provides
    @Singleton
    fun provideDao(db:StoriesDatabase)= db.storiesDao()
}
