package rachman.forniandi.circlegathering.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.activities.MainActivity.Companion.DETAIL_STORY
import rachman.forniandi.circlegathering.databinding.ActivityDetailStoryActivityBinding
import rachman.forniandi.circlegathering.models.allStories.ListStoryItem
import rachman.forniandi.circlegathering.utils.getStringDate

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryActivityBinding
    private var detailDataStory: ListStoryItem?= null
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundleReceive =intent.getBundleExtra(DETAIL_STORY)
        detailDataStory = bundleReceive?.getSerializable(DETAIL_STORY)as ListStoryItem
        parsingAttributeDetailStory(detailDataStory)


        setSupportActionBar(binding.toolbarDetailStory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    private fun parsingAttributeDetailStory(detailDataStory: ListStoryItem?) {
        Log.d("test_detail_2","test $detailDataStory")
        if (detailDataStory != null){
            binding.txtUsernameDetailStory.text = detailDataStory.name
            println("checkUsername" +detailDataStory.name)
            binding.txtDescriptionDetailStory.text = detailDataStory.description
            println("checkDescription" +detailDataStory.description)
            val formatDateTime = getStringDate(detailDataStory.createdAt)
            println("checkCreatedAt"+detailDataStory.createdAt)
            binding.txtDateTimeDetailStory.text = formatDateTime

            Glide
                .with(this@DetailStoryActivity)
                .load(detailDataStory.photoUrl)
                .fitCenter()
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_placeholder)
                .into(binding.imgDetailStory)

            println("checkPhotoUrl" +detailDataStory.photoUrl)
        }
    }

    companion object {
        const val EXTRA_ID_DETAIL_STORY = "extra_id_detail_story"
    }
}