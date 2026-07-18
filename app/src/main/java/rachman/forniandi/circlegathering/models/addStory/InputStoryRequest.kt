package rachman.forniandi.circlegathering.models.addStory

import android.location.Location
import java.io.File

data class InputStoryRequest(
    val imgStory: File,
    val descriptionStory: String,
    val location: Location? = null
)