package rachman.forniandi.circlegathering.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rachman.forniandi.circlegathering.DBRoom.StoriesDatabase
import rachman.forniandi.circlegathering.utils.ConstantsMain.Companion.DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context)=
        Room.databaseBuilder(context,
            StoriesDatabase::class.java,DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDao(db:StoriesDatabase)= db.storiesDao()
}