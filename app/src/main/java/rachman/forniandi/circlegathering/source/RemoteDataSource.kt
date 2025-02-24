package rachman.forniandi.circlegathering.source

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rachman.forniandi.circlegathering.models.addStory.InputStoryRequest
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.models.detailStories.ResponseDetailStory
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

    suspend fun showStoriesWithLocation(authorization:String):Response<ResponseAllStories>{
        val addedBearerToken2 = makeBearerToken(authorization)
        return networkService.getAllStoriesWithLocation(addedBearerToken2)
    }

    suspend fun showDetailStories(authorization:String,id:String):Response<ResponseDetailStory>{
        val addedBearerToken3 = makeBearerToken(authorization)
        return networkService.getDetailStories(addedBearerToken3,id)
    }

    suspend fun addDataStories(bearerToken4:String,
                               storyRequest:InputStoryRequest
                               ):Response<ResponseAddStory>{
        val addedBearerToken = makeBearerToken(bearerToken4)
        val descriptionStory = storyRequest.descriptionStory.toRequestBody("text/plain".toMediaType())
        val imageStoryRequest = storyRequest.imgStory.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageStoryMultipart = MultipartBody.Part.createFormData(
            "photo",
            storyRequest.imgStory.name,
            imageStoryRequest
        )

        var latitudeInput: RequestBody? = null
        var longitudeInput: RequestBody? = null
        storyRequest.location?.let {
            latitudeInput = it.latitude.toString().toRequestBody("text/plain".toMediaType())
            longitudeInput= it.longitude.toString().toRequestBody("text/plain".toMediaType())
        }
        return networkService.insertStories(addedBearerToken,imageStoryMultipart,descriptionStory,latitudeInput,longitudeInput)
    }

    private fun makeBearerToken(token: String): String {
        return ConstantsMain.TOKEN_BEARER+token
    }

}