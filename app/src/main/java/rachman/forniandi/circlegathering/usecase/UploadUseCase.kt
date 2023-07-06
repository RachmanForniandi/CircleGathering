package rachman.forniandi.circlegathering.usecase

import kotlinx.coroutines.flow.Flow
import rachman.forniandi.circlegathering.models.addStory.ResponseAddStory
import rachman.forniandi.circlegathering.models.addStory.StoryUploadBodyInput
import rachman.forniandi.circlegathering.utils.NetworkResult
import retrofit2.Response

interface UploadUseCase {

    fun actionUploadStory(inputBodyStory:StoryUploadBodyInput): Response<ResponseAddStory>
}