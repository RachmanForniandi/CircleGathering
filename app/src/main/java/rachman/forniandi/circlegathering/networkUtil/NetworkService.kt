package rachman.forniandi.circlegathering.networkUtil

import okhttp3.MultipartBody
import okhttp3.RequestBody
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.models.login.ResponseLogin
import rachman.forniandi.circlegathering.models.register.ResponseRegister
import retrofit2.Response
import retrofit2.http.*
import java.sql.SQLInvalidAuthorizationSpecException

interface NetworkService {
    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(@Field("name") name:String,
                     @Field("email") email:String,
                     @Field("password") password:String
    ):Response<ResponseRegister>

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(@Field("email") email:String,
                  @Field("password") password:String):
            Response<ResponseLogin>


    @Multipart
    @POST("stories")
    suspend fun insertStories(
                      @Header("Authorization")token:String,
                      @Part("description")description:RequestBody,
                      @Part("file") file:MultipartBody.Part):Response<ResponseAddStory>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization")authorization:String
    ):Response<ResponseAllStories>

}