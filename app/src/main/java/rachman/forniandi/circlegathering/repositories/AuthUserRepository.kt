package rachman.forniandi.circlegathering.repositories

import dagger.hilt.android.scopes.ActivityRetainedScoped
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.TokenDataSource
import javax.inject.Inject

@ActivityRetainedScoped
class AuthUserRepository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    prefStore:TokenDataSource,
    onlineSource: KeyOnlineSource
){
    val remote = remoteDataSource
    val store = prefStore
    val online = onlineSource
}