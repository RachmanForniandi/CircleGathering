package rachman.forniandi.circlegathering.utils

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import rachman.forniandi.circlegathering.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ConstantsMain {

    companion object{
        const val BASE_URL ="https://story-api.dicoding.dev/v1/"
        const val PREFERENCES_NAME = "story_preferences"
        const val PREFERENCES_BACK_ONLINE = "backOnline"
        const val TOKEN_BEARER = "Bearer "

        const val KEY_FOR_WIDGET_WORKER = "story_work"
        const val NOTIFICATION_ID = "Gathering_notification_id"
        const val NOTIFICATION_NAME = "Gathering"
        const val NOTIFICATION_CHANNEL ="Gathering_channel_01"
        @BindingAdapter("loadImageUrl")
        @JvmStatic
        fun loadImageUrl(imageView: ImageView, imgUrl:String){
            imageView.load(imgUrl){
                crossfade(600)
                error(R.drawable.error_placeholder)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getStringDate(date: String?): String? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputDate = SimpleDateFormat("EEE, dd MMM yyy KK:mm")
        var d: Date? = null
        try {
            d = dateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputDate.format(d)
    }


}