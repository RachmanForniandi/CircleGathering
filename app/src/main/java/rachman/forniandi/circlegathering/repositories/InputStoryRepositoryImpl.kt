package rachman.forniandi.circlegathering.repositories

import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.models.addStory.InputStoryRequest
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.source.RemoteDataSource
import rachman.forniandi.circlegathering.utils.NetworkResult
import javax.inject.Inject

class InputStoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
):InputStoryUseCase {
    override fun doUploadStory(inputStory: InputStoryRequest): Flow<NetworkResult<ResponseAddStory>> {
        TODO("Not yet implemented")
    }

    override fun showStoryWithLocation(): Flow<NetworkResult<List<StoryItem>>> {
        TODO("Not yet implemented")
    }
}