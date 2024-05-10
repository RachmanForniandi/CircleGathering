package rachman.forniandi.circlegathering.models.allStories

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class StoryItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String?,

	@field:SerializedName("createdAt")
	val createdAt: String?,

	@field:SerializedName("name")
	val name: String?,

	@field:SerializedName("description")
	val description: String?,

	@field:SerializedName("lon")
	val lon: Double?,

	@field:SerializedName("id")
	val id: String?,

	@field:SerializedName("lat")
	val lat:  Double?
) :  Serializable
