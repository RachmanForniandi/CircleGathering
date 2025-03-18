package rachman.forniandi.circlegathering.utils

import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory

fun ResponseAddStory.toUploadStoryDomain()=ResponseAddStory(error, message)