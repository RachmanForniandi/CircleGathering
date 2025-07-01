package rachman.forniandi.circlegathering

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.models.login.LoginResult
import rachman.forniandi.circlegathering.models.login.ResponseLogin
import rachman.forniandi.circlegathering.models.register.ResponseRegister

object StoryDummy {

    fun generateDummyResponseStories(): ResponseAllStories {

        val error = false
        val message = "Stories fetched successfully"
        val listStory = mutableListOf<StoryItem>()

        for (i in 0 until 10) {
            val storyItem = StoryItem(
                id= "story-jBOHK1IxT8MxdHCc",
            name="Haruto",
            description= "tes",
            photoUrl= "https://story-api.dicoding.dev/images/stories/photos-1749977160941_9947ac576fbadf030eea.jpg",
            createdAt= "2025-06-15T08:46:00.948Z",
            lat= -1.9332268264771106,
            lon= 103.00781250000001
            )
            listStory.add(storyItem)
        }
        return ResponseAllStories(listStory, error, message)
    }

    fun generateDummyListStoryEntity():List<StoriesEntity>{
        val items = arrayListOf<StoriesEntity>()

        for (i in 0 until 10) {
            val storyDummy = StoriesEntity(
                id = "story-$i",
                name = "user_$i",
                description = "description_$i",
                photoUrl = "https://example.com/photo_$i.jpg",
                createdAt = "2025-06-22T22:46:${10 + i}.161Z",
                lat = -1.0 + i,
                lon = 102.0 + i
            )
            items.add(storyDummy)
        }
        return items
    }




}