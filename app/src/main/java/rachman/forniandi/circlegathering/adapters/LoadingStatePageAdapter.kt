package rachman.forniandi.circlegathering.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ItemLoadingShimmerBinding

class LoadingStatePageAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStatePageAdapter.LoadingStatePageViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStatePageViewHolder {
        val binding =
            ItemLoadingShimmerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStatePageViewHolder(binding, retry)
    }

    override fun onBindViewHolder(
        holder: LoadingStatePageViewHolder,
        loadState: LoadState
    ) {
        holder.bindView(loadState)
    }

    class LoadingStatePageViewHolder (private val binding: ItemLoadingShimmerBinding,retry: () -> Unit):

        RecyclerView.ViewHolder(binding.root) {

            init {
                binding.retryButton.setOnClickListener { retry.invoke() }
            }

        fun bindView(state: LoadState) {
            if (state is LoadState.Error){
                binding.txtErrorMsg.text=
                    binding.root.context.getString(R.string.unable_to_fetch_data_of_stories)
            }
            binding.progressBar1.isVisible = state is LoadState.Loading
            binding.progressBar2.isVisible = state is LoadState.Loading
            binding.retryButton.isVisible = state is LoadState.Error
            binding.txtErrorMsg.isVisible = state is LoadState.Error
        }

    }
}