package rachman.forniandi.circlegathering.repositories

import dagger.hilt.android.scopes.ActivityRetainedScoped
import rachman.forniandi.circlegathering.source.KeyOnlineSource
import rachman.forniandi.circlegathering.source.RemoteDataSource
import javax.inject.Inject

@ActivityRetainedScoped
class AuthUserRepository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    onlineSource: KeyOnlineSource
){
    val remote = remoteDataSource
    val online = onlineSource
}