package rachman.forniandi.circlegathering.models.addStory

import com.google.gson.annotations.SerializedName

data class ResponseAddStory(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)