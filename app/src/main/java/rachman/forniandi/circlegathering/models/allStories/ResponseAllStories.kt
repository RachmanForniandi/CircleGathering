package rachman.forniandi.circlegathering.models.allStories

import com.google.gson.annotations.SerializedName


data class ResponseAllStories(

	@field:SerializedName("listStory")
	val listStory: List<StoryItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

