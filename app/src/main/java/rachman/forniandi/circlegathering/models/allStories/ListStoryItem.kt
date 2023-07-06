package rachman.forniandi.circlegathering.models.allStories

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ListStoryItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	/*@field:SerializedName("lon")
	val lon: Any? = null,*/

	@field:SerializedName("id")
	val id: String? = null,

	/*@field:SerializedName("lat")
	val lat: Any? = null*/
) :  Parcelable