package rachman.forniandi.circlegathering.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.activities.MainActivity.Companion.DETAIL_STORY
import rachman.forniandi.circlegathering.databinding.ActivityDetailStoryActivityBinding
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.animateLoadingProcessData
import rachman.forniandi.circlegathering.utils.getStringDate
import rachman.forniandi.circlegathering.viewModels.DetailStoryViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DetailStoryActivity : AppCompatActivity() {
    private val viewModel: DetailStoryViewModel by viewModels()
    private lateinit var binding: ActivityDetailStoryActivityBinding
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showDetailStory()
        setSupportActionBar(binding.toolbarDetailStory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }


    private fun showDetailStory() {
        val idStory =intent.getStringExtra(DETAIL_STORY)

        viewModel.doShowAllStoriesData(idStory!!)
        viewModel.getDetailStoriesResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    applyLoadProgressStateDetail(false)
                    val feedbackDetail = response.data?.story
                    binding.txtLblId.text = getString(R.string.id,"id",feedbackDetail?.id)
                    println("checkId" +feedbackDetail?.id)
                    binding.txtLblCreatedBy.text = getString(R.string.created_by, "name","",feedbackDetail?.name)
                    println("checkUsername" +feedbackDetail?.name)
                    binding.txtDescriptionDetailStory.text = feedbackDetail?.description
                    println("checkDescription" +feedbackDetail?.description)
                    val formatDateTime = getStringDate(feedbackDetail?.createdAt)
                    println("checkCreatedAt"+feedbackDetail?.createdAt)
                    binding.txtLblDateTime.text = getString(R.string.date_time, "date/time","","",formatDateTime)

                    Glide
                        .with(this@DetailStoryActivity)
                        .load(feedbackDetail?.photoUrl)
                        .fitCenter()
                        .placeholder(R.drawable.place_holder)
                        .error(R.drawable.error_placeholder)
                        .into(binding.imgDetailStory)

                    println("checkPhotoUrl" +feedbackDetail?.photoUrl)
                    
                }
                is NetworkResult.Error -> {
                    applyLoadProgressStateDetail(false)
                    Toast.makeText(
                        this,
                        response.message.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    applyLoadProgressStateDetail(true)
                }
            }
        }
    }
    

    private fun applyLoadProgressStateDetail(onProcess:Boolean){

        if (onProcess){
            binding.maskedViewPgDetail.animateLoadingProcessData(true)
        }else{
            binding.maskedViewPgDetail.animateLoadingProcessData(false)
        }
    }

}