package rachman.forniandi.circlegathering.activities

import android.app.AlertDialog.Builder
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.LoginRegister.LoginRegisterActivity
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.adapters.LoadingStatePageAdapter
import rachman.forniandi.circlegathering.adapters.MainNewAdapter
import rachman.forniandi.circlegathering.databinding.ActivityMainBinding
import rachman.forniandi.circlegathering.utils.NetworkListener
import rachman.forniandi.circlegathering.viewModels.MainViewModel

@OptIn(ExperimentalPagingApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var mainAdapter: MainNewAdapter
    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbarMain)
        //supportActionBar?.setDisplayShowTitleEnabled(false)

        setSwipeRefreshAtMainPage()
        showListStories()
        setUserName()
        observePagingData()

        binding.fabAddStory.setOnClickListener {
            val intentToAddData = Intent(this, FormAddDataActivity::class.java)
            startActivity(intentToAddData)
        }

        binding.btnRetryStory.setOnClickListener {
            mainAdapter.retry()
        }

        /*viewModel.readBackOnline.observe(this) {
            viewModel.backOnline = it
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkListener = NetworkListener()
                networkListener.checkNetworkAvailability(this@MainActivity)
                    .collect { status ->
                        Log.d("NetworkListener", status.toString())
                        viewModel.networkStatus = status
                        viewModel.showNetworkStatus()
                    }
            }
        }*/
    }


    private fun observePagingData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllStoriesPerPages().collectLatest { pagingData ->
                    viewModel.recyclerViewState = binding.listDataStories.layoutManager?.onSaveInstanceState()
                    mainAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun setUserName() {
        viewModel.getUserName().observe(this) { user ->
            binding.lblGreetingUser.text = getString(R.string.welcome, user)
        }
    }

    private fun setSwipeRefreshAtMainPage() {
        binding.swipeRefreshMain.setOnRefreshListener {
            startSwipeRefresh()
            mainAdapter.refresh()
        }
    }

    private fun showListStories() {
        mainAdapter = MainNewAdapter { id -> handleToDetail(id) }
        binding.listDataStories.adapter = mainAdapter.withLoadStateFooter(
            footer = LoadingStatePageAdapter { mainAdapter.retry() }
        )

        mainAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                startSwipeRefresh()
                showShimmerEffect()
            } else {
                stopSwipeRefresh()
                hideShimmerEffect()
                val errorState = loadState.source.refresh as? LoadState.Error
                val endOfPaginationReached = loadState.append.endOfPaginationReached


                if (errorState != null) {
                    // Error terjadi saat refresh (misalnya network error)
                    showErrorState(true)
                    binding.listDataStories.visibility = View.GONE // << penting
                } else if (endOfPaginationReached && mainAdapter.itemCount == 0) {
                    // Tidak ada data setelah sukses memuat
                    showEmptyState() // Bisa tampilkan UI “Belum ada data”
                } else {
                    showErrorState(false)
                    binding.listDataStories.visibility = View.VISIBLE
                }
                showErrorState(errorState != null)
            }
        }
    }

    private fun showEmptyState() {
        binding.imgError.visibility = View.VISIBLE
        binding.txtError.visibility = View.VISIBLE
        binding.txtError.text = getString(R.string.data_not_available)
        binding.btnRetryStory.visibility = View.GONE
    }



    private fun showErrorState(show: Boolean) {
        binding.imgError.visibility = if (show) View.VISIBLE else View.GONE
        binding.txtError.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnRetryStory.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnRetryStory.isClickable = show
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleToDetail(id: String?) {
        val toDetailStory = Intent(this, DetailStoryActivity::class.java)
        toDetailStory.putExtra(DETAIL_STORY, id)
        startActivity(toDetailStory)
    }

    private fun startSwipeRefresh() {
        binding.swipeRefreshMain.isRefreshing = true
    }

    private fun stopSwipeRefresh() {
        binding.swipeRefreshMain.isRefreshing = false
    }

    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.listDataStories.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.listDataStories.visibility = View.VISIBLE
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        Builder(this)
            .setTitle(getString(R.string.exit))
            .setMessage(getString(R.string.are_you_sure_do_you_want_to_exit))
            .setNegativeButton(getString(R.string.no), null)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                super@MainActivity.onBackPressed()
            }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.signOutUser()
                val intentToAuth = Intent(this, LoginRegisterActivity::class.java)
                intentToAuth.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentToAuth)
                finish()
                true
            }

            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }

            R.id.menu_maps -> {
                startActivity(Intent(this, ExploreMapsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val DETAIL_STORY = "detail_story"
    }
}