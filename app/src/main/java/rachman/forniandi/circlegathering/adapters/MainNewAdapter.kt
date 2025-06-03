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

class MainNewAdapter (
    private val onItemClicked: (id: String?) -> Unit
): PagingDataAdapter<StoriesEntity, MainNewAdapter.MainNewHolder>(DIFF_CALLBACK) {

    inner class MainNewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoriesEntity?) {
            binding.txtTitleStory.text = item?.description
            binding.txtTimeStoryElapsed.text = item?.createdAt.getTimeElapseFormat()
            Glide.with(binding.root.context)
                .load(item?.photoUrl)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_placeholder)
                .into(binding.imgStory)
            itemView.setOnClickListener {
                onItemClicked(item?.id)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainNewHolder {
        val bindingView = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainNewHolder(bindingView)
    }

    override fun onBindViewHolder(holder: MainNewHolder, position: Int) {
        val storiesEntity = getItem(position)
        holder.bind(storiesEntity)
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