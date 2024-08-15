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

    companion object {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"
        const val PREFERENCES_NAME = "story_preferences"
        const val PREFERENCES_BACK_ONLINE = "backOnline"
        const val TOKEN_BEARER = "Bearer "


        const val DATABASE_NAME = "stories_database"
   }

}