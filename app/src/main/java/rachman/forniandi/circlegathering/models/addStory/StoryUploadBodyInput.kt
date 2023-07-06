package rachman.forniandi.circlegathering.models.addStory

import java.io.File

data class StoryUploadBodyInput(
    val image: File,
    val description: String
)
