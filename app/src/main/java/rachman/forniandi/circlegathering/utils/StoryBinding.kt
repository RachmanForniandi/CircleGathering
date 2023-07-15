package rachman.forniandi.circlegathering.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.adapters.MainAdapter
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories

class StoryBinding {

    companion object{
        @BindingAdapter("readApiResponse", "readDatabase",requireAll = true)
        @JvmStatic
        fun handleReadDataErrors(textView: TextView,
                                 imageView: ImageView,
                                 recyclerView: RecyclerView,
                                 mainAdapter: MainAdapter?,
                                 dbStoriesEntity: List<StoriesEntity>?,
                                 apiResponse:NetworkResult<ResponseAllStories>?,
        ) {
            if (apiResponse is NetworkResult.Error && dbStoriesEntity.isNullOrEmpty()) {
                textView.visibility =View.VISIBLE
                textView.text = apiResponse.message.toString()

                imageView.visibility =View.VISIBLE
                recyclerView.visibility = View.INVISIBLE
            }else{
                textView.visibility =View.INVISIBLE
                imageView.visibility =View.INVISIBLE
                recyclerView.visibility= View.VISIBLE
                dbStoriesEntity.let { mainAdapter?.setData(it as ResponseAllStories) }
            }
            /*when(view){
                is RecyclerView->{
                    view.isInvisible =apiResponse is NetworkResult.Error
                }

                is ImageView ->{
                    view.isVisible =apiResponse is NetworkResult.Error
                }
                is TextView ->{
                    view.isVisible =apiResponse is NetworkResult.Error
                    view.text = apiResponse?.message.toString()
                }
            }*/
        }

        /*@BindingAdapter("readApiResponseForText", requireAll = true)
        @JvmStatic
        fun errorTextViewVisibility(textView: TextView, apiResponse:NetworkResult<ResponseAllStories>?) {
            if (apiResponse is NetworkResult.Error) {
                textView.visibility = View.VISIBLE
                textView.text = apiResponse.message.toString()
            } else if (apiResponse is NetworkResult.Loading) {
                textView.visibility = View.INVISIBLE
            } else if (apiResponse is NetworkResult.Success) {
                textView.visibility = View.INVISIBLE
            }
        }*/
    }


}
