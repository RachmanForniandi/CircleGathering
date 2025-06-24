package rachman.forniandi.circlegathering

import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.models.allStories.StoryItem

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
                id = "story-A35X5ujzaW6p5fSy",
                name = "note794",
                description = "zxczxczxczxczxc",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1750632397153_053fc3f022b801a5bf0e.jpg",
                createdAt = "2025-06-22T22:46:37.161Z",
                lat = -1.198154140377501,
                lon = 102.39608945297535
            )
            items.add(storyDummy)
        }
        return items
    }



}