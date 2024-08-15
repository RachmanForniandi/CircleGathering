package rachman.forniandi.circlegathering.repositories

import dagger.hilt.android.scopes.ViewModelScoped
import rachman.forniandi.circlegathering.source.RemoteDataSource
import javax.inject.Inject

@ViewModelScoped
class MainRepository@Inject constructor(
    remoteDataSource: RemoteDataSource
){
    val remoteMain = remoteDataSource
}
