package rachman.forniandi.circlegathering.models.detailStories


import com.google.gson.annotations.SerializedName

data class Story(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("lat")
    val lat: Any,
    @SerializedName("lon")
    val lon: Any,
    @SerializedName("name")
    val name: String,
    @SerializedName("photoUrl")
    val photoUrl: String
)