package rachman.forniandi.circlegathering.models.detailStories


import com.google.gson.annotations.SerializedName
import rachman.forniandi.circlegathering.models.allStories.StoryItem

data class ResponseDetailStory(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("story")
    val story: StoryItem
)