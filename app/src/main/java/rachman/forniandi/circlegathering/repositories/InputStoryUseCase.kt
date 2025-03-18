package rachman.forniandi.circlegathering.repositories

import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.models.addStory.InputStoryRequest
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.utils.NetworkResult

interface InputStoryUseCase {
    fun doUploadStory(inputStory:InputStoryRequest): Flow<NetworkResult<ResponseAddStory>>

    fun showStoryWithLocation():Flow<NetworkResult<List<StoryItem>>>
}