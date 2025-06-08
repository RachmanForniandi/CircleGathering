package rachman.forniandi.circlegathering.activities

import android.app.AlertDialog.Builder
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import androidx.paging.PagingData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.LoginRegister.LoginRegisterActivity
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.adapters.LoadingStatePageAdapter
import rachman.forniandi.circlegathering.adapters.MainNewAdapter
import rachman.forniandi.circlegathering.dBRoom.entities.StoriesEntity
import rachman.forniandi.circlegathering.databinding.ActivityMainBinding
import rachman.forniandi.circlegathering.utils.NetworkListener
import rachman.forniandi.circlegathering.viewModels.MainNewViewModel

@OptIn(ExperimentalPagingApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainNewViewModel by viewModels()
    private lateinit var mainAdapter: MainNewAdapter
    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initAdapter()
        observePagingData()
        observeUser()
        observeNetwork()

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, FormAddDataActivity::class.java))
        }

        binding.swipeRefreshMain.setOnRefreshListener {
            mainAdapter.refresh()
        }

        binding.btnRetryStory.setOnClickListener {
            mainAdapter.retry()
        }

        viewModel.readBackOnline.observe(this) {
            viewModel.backOnline = it
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.recyclerViewState?.let {
            binding.listDataStories.layoutManager?.onRestoreInstanceState(it)
        }
    }

    private fun initAdapter() {
        mainAdapter = MainNewAdapter { id -> handleToDetail(id) }

        binding.listDataStories.adapter = mainAdapter.withLoadStateFooter(
            footer = LoadingStatePageAdapter { mainAdapter.retry() }
        )

        mainAdapter.addLoadStateListener { loadState ->
            val isLoading = loadState.refresh is LoadState.Loading
            val isError = loadState.refresh is LoadState.Error

            if (isLoading) {
                startSwipeRefresh()
                showShimmerEffect()
            } else {
                stopSwipeRefresh()
                hideShimmerEffect()
            }

            if (isError) {
                binding.imgError.visibility = View.VISIBLE
                binding.txtError.visibility = View.VISIBLE
                binding.btnRetryStory.visibility = View.VISIBLE
            } else {
                binding.imgError.visibility = View.GONE
                binding.txtError.visibility = View.GONE
                binding.btnRetryStory.visibility = View.GONE
            }
        }
    }

    private fun observePagingData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllStoriesPerPages().collectLatest { pagingData ->
                    val savedState = binding.listDataStories.layoutManager?.onSaveInstanceState()
                    mainAdapter.submitData(lifecycle, pagingData)
                    viewModel.recyclerViewState = savedState
                }
            }
        }
    }

    private fun observeUser() {
        viewModel.getUserName().observe(this) { user ->
            binding.lblGreetingUser.text = getString(R.string.welcome, user)
        }
    }

    private fun observeNetwork() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkListener = NetworkListener()
                networkListener.checkNetworkAvailability(this@MainActivity).collect { status ->
                    Log.d("NetworkListener", status.toString())
                    viewModel.networkStatus = status
                    viewModel.showNetworkStatus()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleToDetail(id: String?) {
        val toDetailStory = Intent(this@MainActivity, DetailStoryActivity::class.java)
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
        Builder(this)
            .setTitle(getString(R.string.exit))
            .setMessage(getString(R.string.are_you_sure_do_you_want_to_exit))
            .setNegativeButton(getString(R.string.no), null)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                super.onBackPressed()
            }
            .create().show()
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
                intentToAuth.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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