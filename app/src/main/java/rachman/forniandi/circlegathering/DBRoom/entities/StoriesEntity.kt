package rachman.forniandi.circlegathering.DBRoom.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import rachman.forniandi.circlegathering.models.allStories.ListStoryItem
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.utils.ConstantsMain.Companion.STORY_TABLE

@Entity(tableName = STORY_TABLE)
class StoriesEntity (var listStoryItem: ResponseAllStories){

    @PrimaryKey(autoGenerate = false)
    var id:Int =0
}