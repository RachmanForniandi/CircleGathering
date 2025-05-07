package rachman.forniandi.circlegathering.dBRoom.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_table")
class StoriesEntity (
    @PrimaryKey
    val id: String,
    val name: String? = null,
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val description: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
)
