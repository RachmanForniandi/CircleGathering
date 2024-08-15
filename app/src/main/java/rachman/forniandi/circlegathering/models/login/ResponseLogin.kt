package rachman.forniandi.circlegathering.models.login

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

	@field:SerializedName("loginResult")
	val loginResult: LoginResult?,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)