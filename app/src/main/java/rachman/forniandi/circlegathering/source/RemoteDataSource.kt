package rachman.forniandi.circlegathering.source

import okhttp3.MultipartBody
import okhttp3.RequestBody
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.models.login.ResponseLogin
import rachman.forniandi.circlegathering.models.register.ResponseRegister
import rachman.forniandi.circlegathering.networkUtil.NetworkService
import rachman.forniandi.circlegathering.utils.ConstantsMain
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val networkService: NetworkService) {

    suspend fun doLogin(email: String, password: String): Response<ResponseLogin >{
       return networkService.loginUser(email, password)
    }

    suspend fun doRegister(username:String,email: String, password: String): Response<ResponseRegister>{
        return networkService.registerUser(username,email, password)
    }

    suspend fun showStories(authorization:String):Response<ResponseAllStories>{
        val addedBearerToken = makeBearerToken(authorization)
        return networkService.getAllStories(addedBearerToken)
    }

    suspend fun addDataStories(bearerToken:String,
                               file:MultipartBody.Part,
                               description:RequestBody):Response<ResponseAddStory>{
        val addedBearerToken = makeBearerToken(bearerToken)
        return networkService.insertStories(addedBearerToken,file,description)
    }

    private fun makeBearerToken(token: String): String {
        return ConstantsMain.TOKEN_BEARER+token
    }
    /*private fun makeBearerTokenToRequestBody(token: RequestBody): RequestBody {
        val convertTokenRequestBody= (ConstantsMain.TOKEN_BEARER+token).toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return convertTokenRequestBody
    }*/
}