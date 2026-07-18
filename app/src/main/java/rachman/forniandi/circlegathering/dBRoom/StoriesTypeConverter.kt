package rachman.forniandi.circlegathering.dBRoom

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rachman.forniandi.circlegathering.models.allStories.StoryItem

class StoriesTypeConverter {
    var gson= Gson()
    @TypeConverter
    fun fromStoryItem(storyItem: StoryItem): String {
        return gson.toJson(storyItem)
    }

    @TypeConverter
    fun toStoryItem(data: String): StoryItem {
        val type = object : TypeToken<StoryItem>() {}.type
        return gson.fromJson(data, type)
    }
}
