package rachman.forniandi.circlegathering.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import rachman.forniandi.circlegathering.databinding.ItemStoryBinding
import rachman.forniandi.circlegathering.models.allStories.ListStoryItem
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.networkUtil.StoryDiffUtil
import rachman.forniandi.circlegathering.utils.ConstantsMain

class MainAdapter(private val listStory: ArrayList<ListStoryItem>):RecyclerView.Adapter<MainAdapter.MainHolder>() {

    private var story= emptyList<ListStoryItem>()
    private var onClickListener: OnStoryClickListener?= null

    class MainHolder(private val view: ItemStoryBinding): RecyclerView.ViewHolder(view.root){

        fun bind(resultStory:ListStoryItem){
            view.dataStory = resultStory
            //view.convertFormatDateTime = ConstantsMain()
            view.executePendingBindings()
        }
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
        holder.bind(storyItem)
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position,storyItem)

        }
    }

    fun setOnClickListener(onClickListener: OnStoryClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnStoryClickListener {
        fun onClick(position: Int, story: ListStoryItem)
    }

    fun setData(storyData: ResponseAllStories){
        val dataDiffUtil = StoryDiffUtil(story,storyData.listStory)
        val diffUtilResult = DiffUtil.calculateDiff(dataDiffUtil)
        story = storyData.listStory
        diffUtilResult.dispatchUpdatesTo(this)
    }
}