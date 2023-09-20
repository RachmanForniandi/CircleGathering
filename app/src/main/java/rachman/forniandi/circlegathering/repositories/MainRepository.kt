package rachman.forniandi.circlegathering.repositories

import dagger.hilt.android.scopes.ViewModelScoped
import rachman.forniandi.circlegathering.source.LocalDataSource
import rachman.forniandi.circlegathering.source.RemoteDataSource
import javax.inject.Inject

@ViewModelScoped
class MainRepository@Inject constructor(
    remoteDataSource: RemoteDataSource
    //localDataSource: LocalDataSource
){
    val remoteMain = remoteDataSource
    //val localMain = localDataSource
}
