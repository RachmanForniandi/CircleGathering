package rachman.forniandi.circlegathering.utils

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import rachman.forniandi.circlegathering.DBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.adapters.MainAdapter
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories

class StoryBinding {

    companion object {
        @BindingAdapter("checkValidateDataVisibility",requireAll = true)
        @JvmStatic
        fun checkValidateDataVisibility(
            view: View,
            apiResponse:NetworkResult<ResponseAllStories>
        ) {
            when (apiResponse) {
                is NetworkResult.Loading -> {
                    when (view) {
                        is ImageView -> {
                            view.visibility = View.GONE
                        }

                        /*is Button -> {
                            view.visibility = View.GONE
                            view.isClickable = false
                        }*/

                        is TextView -> {
                            view.visibility = View.GONE
                        }
                    }
                }

                is NetworkResult.Success -> {
                    when (view) {
                        is ImageView -> {
                            view.visibility = View.GONE
                        }

                        /*is Button -> {
                            view.visibility = View.GONE
                            view.isClickable = false
                        }*/

                        is TextView -> {
                            view.visibility = View.GONE
                        }
                    }
                }

                is NetworkResult.Error -> {
                    when (view) {
                        is ImageView -> {
                            view.visibility = View.VISIBLE
                        }

                        /*is Button -> {
                            view.visibility = View.VISIBLE
                            view.isClickable = true
                        }*/

                        is TextView -> {
                            view.visibility = View.VISIBLE
                        }
                    }
                }

            }

        }

    }

}
