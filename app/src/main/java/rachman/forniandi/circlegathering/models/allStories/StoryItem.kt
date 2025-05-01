package rachman.forniandi.circlegathering.models.allStories

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import rachman.forniandi.circlegathering.utils.ConstantsMain.Companion.STORY_TABLE
import java.io.Serializable


@Entity(tableName = STORY_TABLE)
data class StoryItem(
	@PrimaryKey
	@field:SerializedName("id")
	val id: String?,

	@field:SerializedName("name")
	val name: String?,

	@field:SerializedName("description")
	val description: String?,

	@field:SerializedName("photoUrl")
	val photoUrl: String?,

	@field:SerializedName("createdAt")
	val createdAt: String?,

	@field:SerializedName("lat")
	val lat:  Double?,

	@field:SerializedName("lon")
	val lon: Double?

) :  Serializable
