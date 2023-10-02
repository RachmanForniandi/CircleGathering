package rachman.forniandi.circlegathering.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import retrofit2.Response

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> Response<ResponseAllStories>,
    crossinline saveFetchResult: suspend (Response<ResponseAllStories>) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
//First step, fetch data from the local cache
    val data = query().first()

    //If shouldFetch returns true,
    val resource = if (shouldFetch(data)) {

        //Dispatch a message to the UI that you're doing some background work
        emit(NetworkResult.Loading())

        try {

            //make a networking call
            val resultType = fetch()

            //save it to the database
            saveFetchResult(resultType)

            //Now fetch data again from the database and Dispatch it to the UI
            query().map { NetworkResult.Success(it) }

        } catch (e:Exception) {

            //Dispatch any error emitted to the UI, plus data emmited from the Database
            query().map { NetworkResult.Error("", it) }

        }

        //If should fetch returned false
    } else {
        //Make a query to the database and Dispatch it to the UI.
        query().map { NetworkResult.Success(it) }
    }

    //Emit the resource variable
    emitAll(resource)
}