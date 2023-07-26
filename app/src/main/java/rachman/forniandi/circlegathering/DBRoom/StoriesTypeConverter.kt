package rachman.forniandi.circlegathering.DBRoom

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rachman.forniandi.circlegathering.models.allStories.ListStoryItem
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories

class StoriesTypeConverter {
    var gson= Gson()
    @TypeConverter
    fun storiesToString(allStories: ResponseAllStories):String{
        return gson.toJson(allStories)
    }

    @TypeConverter
    fun stringToStories(data:String):ResponseAllStories{
        val listType= object : TypeToken<ResponseAllStories>(){}.type
        return gson.fromJson(data,listType)
    }

    @TypeConverter
    fun listStoriesToString(result: ListStoryItem):String{
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToString(data: String):ListStoryItem{
        val listType = object : TypeToken<ListStoryItem>() {}.type
        return gson.fromJson(data,listType)
    }
}