package rachman.forniandi.circlegathering.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rachman.forniandi.circlegathering.dBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.utils.ConstantsMain.Companion.DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

/*@Singleton
    @Provides
    fun provideStoryDao(@ApplicationContext context: Context):StoriesDao {
        val storyDb = StoriesDatabase.getInstanceDb(context)
        return storyDb.storiesDao()
    }



@Singleton
    @Provides
    fun provideDao(db:StoriesDatabase)= db.storiesDao()*/

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context)=
        Room.databaseBuilder(context,
            StoriesDatabase::class.java,DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDao(db:StoriesDatabase)= db.storiesDao()
}
