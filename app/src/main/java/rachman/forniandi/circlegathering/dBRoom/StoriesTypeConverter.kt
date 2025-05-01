package rachman.forniandi.circlegathering.dBRoom

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rachman.forniandi.circlegathering.models.allStories.StoryItem

class StoriesTypeConverter {
    var gson= Gson()
    @TypeConverter
    fun storiesToString(allStories: StoryItem):String{
        return gson.toJson(allStories)
    }

    @TypeConverter
    fun stringToStories(data:String):StoryItem{
        val listType= object : TypeToken<StoryItem>(){}.type
        return gson.fromJson(data,listType)
    }

    @TypeConverter
    fun listStoriesToString(result: StoryItem):String{
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToString(data: String):StoryItem{
        val listType = object : TypeToken<StoryItem>() {}.type
        return gson.fromJson(data,listType)
    }
}
