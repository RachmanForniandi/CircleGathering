package rachman.forniandi.circlegathering.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.databinding.ItemStoryBinding
import rachman.forniandi.circlegathering.utils.getTimeElapseFormat

class MainNewAdapter : PagingDataAdapter<StoriesEntity, MainNewAdapter.MainNewHolder>(DIFF_CALLBACK) {

    private var onClickListener: OnStoryClickListener? = null

    class MainNewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoriesEntity, listener: OnStoryClickListener?) {
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
        val StoriesEntity = getItem(position)
        StoriesEntity?.let {
            holder.bind(it, onClickListener)
        }
    }


    interface OnStoryClickListener {
        fun onClick(position: Int, story: StoriesEntity)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoriesEntity>() {
            override fun areItemsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}