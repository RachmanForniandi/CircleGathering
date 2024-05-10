package rachman.forniandi.circlegathering.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ItemStoryBinding
import rachman.forniandi.circlegathering.models.allStories.StoryItem
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.networkUtil.StoryDiffUtil

class MainAdapter(private val mContext:Context):RecyclerView.Adapter<MainAdapter.MainHolder>() {

    private var story= emptyList<StoryItem>()
    private var onClickListener: OnStoryClickListener?= null

    class MainHolder(view: ItemStoryBinding): RecyclerView.ViewHolder(view.root){
        val imgStoryItem = view.imgStory
        val titlePictureItem = view.txtTitleStory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val bindingView = ItemStoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MainHolder(bindingView)
    }

    override fun getItemCount(): Int {
        return story.size
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val storyItem = story[position]
        holder.titlePictureItem.text= storyItem.description
        Glide.with(mContext)
            .load(storyItem.photoUrl)
            .placeholder(R.drawable.place_holder)
            .error(R.drawable.error_placeholder)
            .into(holder.imgStoryItem)

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position,storyItem)

        }
    }

    fun setOnClickListener(onClickListener: OnStoryClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnStoryClickListener {
        fun onClick(position: Int, story: StoryItem)
    }

    fun setData(storyData: ResponseAllStories){
        val dataDiffUtil = StoryDiffUtil(story,storyData.listStory)
        val diffUtilResult = DiffUtil.calculateDiff(dataDiffUtil)
        story = storyData.listStory
        diffUtilResult.dispatchUpdatesTo(this)
    }
}