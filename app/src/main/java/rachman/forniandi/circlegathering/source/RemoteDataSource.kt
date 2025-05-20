package rachman.forniandi.circlegathering.source

import okhttp3.MultipartBody
import okhttp3.RequestBody
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.models.detailStories.ResponseDetailStory
import rachman.forniandi.circlegathering.models.login.ResponseLogin
import rachman.forniandi.circlegathering.models.register.ResponseRegister
import rachman.forniandi.circlegathering.networkUtil.NetworkService
import rachman.forniandi.circlegathering.utils.ConstantsMain
import rachman.forniandi.circlegathering.utils.addedBearerToToken
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
        val addedBearerToken = addedBearerToToken(authorization)
        return networkService.getAllStories(addedBearerToken)
    }

    suspend fun showStoriesWithLocation(authorization:String):Response<ResponseAllStories>{
        val addedBearerTokenStoryLocation= addedBearerToToken(authorization)
        return networkService.getAllStoriesWithLocation(addedBearerTokenStoryLocation)
    }

    suspend fun showStoriesPerPages(authorization:String,page:Int? = null,size:Int? = null):Response<ResponseAllStories>{
        val addedBearerTokenStoryPages = addedBearerToToken(authorization)
        return networkService.getNewAllStories(addedBearerTokenStoryPages,page,size)
    }

    suspend fun showDetailStories(authorization:String,id:String):Response<ResponseDetailStory>{
        val addedBearerTokenDetail = addedBearerToToken(authorization)
        return networkService.getDetailStories(addedBearerTokenDetail,id)
    }

    suspend fun addDataStories(bearerTokenInputStory:String,
                               file:MultipartBody.Part,
                               description:RequestBody,
                               lat: RequestBody? = null,
                               lon: RequestBody? = null
    ):Response<ResponseAddStory>{
        val addedBearerTokenInputStory = addedBearerToToken(bearerTokenInputStory)

        return networkService.insertStories(addedBearerTokenInputStory,file, description,lat,lon)
    }

}