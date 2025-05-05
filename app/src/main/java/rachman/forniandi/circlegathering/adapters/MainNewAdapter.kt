package rachman.forniandi.circlegathering.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ItemStoryBinding
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.utils.getTimeElapseFormat

class MainNewAdapter : PagingDataAdapter<StoryItem, MainNewAdapter.MainNewHolder>(DIFF_CALLBACK) {

    private var onClickListener: OnStoryClickListener? = null

    class MainNewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryItem, listener: OnStoryClickListener?) {
            binding.txtTitleStory.text = item.description
            binding.txtTimeStoryElapsed.text = item.createdAt.getTimeElapseFormat()
            Glide.with(binding.root.context)
                .load(item.photoUrl)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_placeholder)
                .into(binding.imgStory)

            binding.root.setOnClickListener {
                listener?.onClick(bindingAdapterPosition, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainNewHolder {
        val bindingView = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainNewHolder(bindingView)
    }

    override fun onBindViewHolder(holder: MainNewHolder, position: Int) {
        val storyItem = getItem(position)
        storyItem?.let {
            holder.bind(it, onClickListener)
        }
    }


    interface OnStoryClickListener {
        fun onClick(position: Int, story: StoryItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}