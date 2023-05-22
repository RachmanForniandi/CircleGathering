package rachman.forniandi.circlegathering.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories

class StoryBinding {

    companion object{
        @BindingAdapter("readApiResponse", requireAll = true)
        @JvmStatic
        fun handleReadDataErrors(view: View, apiResponse:NetworkResult<ResponseAllStories>?) {
            when(view){
                is ImageView ->{
                    view.isVisible =apiResponse is NetworkResult.Error
                }
                is TextView ->{
                    view.isVisible =apiResponse is NetworkResult.Error
                    view.text = apiResponse?.message.toString()
                }
            }
        }

        @BindingAdapter("readApiResponseForText", requireAll = true)
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
        }
    }


}
