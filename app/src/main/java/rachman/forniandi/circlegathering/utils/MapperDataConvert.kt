package rachman.forniandi.circlegathering.utils

import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.models.allStories.StoryItem

fun List<StoryItem>.toStoryEntity() = map {
    it.id?.let { id -> StoriesEntity(id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon) }
}