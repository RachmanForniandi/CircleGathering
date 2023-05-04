package rachman.forniandi.circlegathering.repositories

import dagger.hilt.android.scopes.ActivityRetainedScoped
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.source.SessionDataSource
import javax.inject.Inject

@ActivityRetainedScoped
class AuthUserRepository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    prefStore:SessionDataSource,
    onlineSource: KeyOnlineSource
){
    val remote = remoteDataSource
    val store = prefStore
    val online = onlineSource
}