package rachman.forniandi.circlegathering.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
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


        const val DATABASE_NAME = "stories_database"
        const val STORY_TABLE ="story_table"
        const val KEY_FOR_WIDGET_WORKER = "story_work"
        const val NOTIFICATION_ID = "Gathering_notification_id"
        const val NOTIFICATION_NAME = "Gathering"
        const val NOTIFICATION_CHANNEL ="Gathering_channel_01"
        @BindingAdapter("loadImageUrl")
        @JvmStatic
        fun loadImageUrl(imageView: ImageView, imgUrl:String){
            /*imageView.load(imgUrl){
                crossfade(600)
                placeholder(R.drawable.place_holder)
                error(R.drawable.error_placeholder)
            }*/
            //imgUrl.let {
                    /*Glide
                        .with(imageView.context)
                        .load(imgUrl)
                        .centerCrop()
                        .timeout(1500)
                        .placeholder(R.drawable.place_holder)
                        .error(R.drawable.error_placeholder)
                        .into(imageView)*/
                /*Glide.with(imageView.context)
                    .load(imgUrl)
                    .centerCrop()
                    .timeout(1500)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.place_holder)
                        .error(R.drawable.error_placeholder))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d("aa","===${e}")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(imageView)*/
                //}
            Picasso
                .get()
                .load(imgUrl)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_placeholder)
                .into(imageView)
        }
    }

    suspend fun <T> Flow<List<T>>.flattenToList() =
        flatMapConcat { it.asFlow() }.toList()

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