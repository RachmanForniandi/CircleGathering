package rachman.forniandi.circlegathering.repositories

import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.TokenDataSource
import javax.inject.Inject

@ViewModelScoped
class MainRepository@Inject constructor(
    remoteDataSource: RemoteDataSource
){
    val remoteMain = remoteDataSource
}
