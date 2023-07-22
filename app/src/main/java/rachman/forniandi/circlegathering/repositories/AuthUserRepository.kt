package rachman.forniandi.circlegathering.repositories

import dagger.hilt.android.scopes.ActivityRetainedScoped
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import javax.inject.Inject

@ActivityRetainedScoped
class AuthUserRepository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    onlineSource: DataStoreRepository
){
    val remote = remoteDataSource
    val online = onlineSource
}