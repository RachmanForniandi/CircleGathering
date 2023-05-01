package rachman.forniandi.circlegathering.source

import okhttp3.MultipartBody
import okhttp3.RequestBody
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.models.login.ResponseLogin
import rachman.forniandi.circlegathering.models.register.ResponseRegister
import rachman.forniandi.circlegathering.networkUtil.NetworkService
import rachman.forniandi.circlegathering.utils.ConstantsMain
import rachman.forniandi.circlegathering.utils.TokenDataSource
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val networkService: NetworkService,
private val tokenDataSource: TokenDataSource) {

    suspend fun doLogin(email: String, password: String): Response<ResponseLogin >{
       return networkService.loginUser(email, password)
    }

    suspend fun doRegister(username:String,email: String, password: String): Response<ResponseRegister>{
        return networkService.registerUser(username,email, password)
    }

    suspend fun keepAuthToken(token: String) {
        tokenDataSource.saveTheAuthToken(token)
    }

    suspend fun keepDataUsername(name: String) {
        tokenDataSource.saveUsername(name)
    }

    suspend fun showStories(authorization:String):Response<ResponseAllStories>{
        val addedBearerToken = makeBearerToken(authorization)
        return networkService.getAllStories(addedBearerToken)
    }

    suspend fun addDataStories(bearerToken:String,description:RequestBody,file:MultipartBody.Part):Response<ResponseAddStory>{
        return networkService.insertStories(bearerToken,description, file)
    }

    private fun makeBearerToken(token: String): String {
        return ConstantsMain.TOKEN_BEARER+token
    }
}