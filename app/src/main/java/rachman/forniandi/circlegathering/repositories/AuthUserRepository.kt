package rachman.forniandi.circlegathering.repositories

import dagger.hilt.android.scopes.ActivityRetainedScoped
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.SessionPrefSource
import javax.inject.Inject

@ActivityRetainedScoped
class AuthUserRepository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    onlineSource: SessionPrefSource
){
    val remote = remoteDataSource
    val online = onlineSource
}