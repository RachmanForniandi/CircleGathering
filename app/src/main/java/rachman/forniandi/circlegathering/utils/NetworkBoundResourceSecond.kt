package rachman.forniandi.circlegathering.utils

import android.widget.RemoteViews.RemoteResponse
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.cache.DiskCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.networkUtil.NetworkService
import rachman.forniandi.circlegathering.source.LocalDataSource
import rachman.forniandi.circlegathering.source.RemoteDataSource

class NetworkBoundResourceSecond<ResultType, RequestType>(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val networkRequest: suspend () -> NetworkService,
    private val mapToLocal: (RequestType) -> ResultType,
    private val shouldFetch: (Flow<List<StoriesEntity>>) -> Boolean = { true }
) {
    fun asFlow(): Flow<Resource<ResultType>> = flow {
        val cachedData = localDataSource.readDbStories()
        if (shouldFetch(cachedData)){
            emit(NetworkResult.Loading())
            try {

            }catch (e: Exception) {

            }
        }else{

        }
    }.flowOn(Dispatchers.IO)
}