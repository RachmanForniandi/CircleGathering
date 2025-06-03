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


        showListStories()
        setUserName()

        setSwipeRefreshAtMainPage()
        requestDataRemoteStories()
        binding.fabAddStory.setOnClickListener {
            val intentToAddData = Intent(this,FormAddDataActivity::class.java)
            startActivity(intentToAddData)
        }
        binding.swipeRefreshMain.isRefreshing = true

        binding.btnRetryStory.setOnClickListener {
            requestDataRemoteStories()
        }

        viewModel.readBackOnline.observe(this){
            viewModel.backOnline=it
        }


        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                networkListener = NetworkListener()
                networkListener.checkNetworkAvailability(this@MainActivity)
                    .collect { status->
                        Log.d("NetworkListener",status.toString())
                        viewModel.networkStatus = status
                        viewModel.showNetworkStatus()
                    }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (viewModel.recyclerViewState != null){
            binding.listDataStories.layoutManager?.onRestoreInstanceState(viewModel.recyclerViewState)
        }
    }

    private fun setUserName() {
        viewModel.getUserName().observe(this) { user ->
            binding.lblGreetingUser.text = getString(R.string.welcome, user)
        }
    }


    private fun setSwipeRefreshAtMainPage() {
        binding.swipeRefreshMain.setOnRefreshListener {
            requestDataRemoteStories()
            hideShimmerEffect()
        }
    }

    private fun requestDataRemoteStories() {
        lifecycleScope.launch {
            viewModel.getAllStoriesPerPages().collect{ result ->
                updateDataPerPages(result)
            }
        }

    }

    private fun updateDataPerPages(data: PagingData<StoriesEntity>) {
        val listStoriesState = binding.listDataStories.layoutManager?.onSaveInstanceState()
        mainAdapter.submitData(lifecycle,data)

        binding.listDataStories.layoutManager?.onRestoreInstanceState(listStoriesState)
    }

    private fun showListStories() {
        mainAdapter = MainNewAdapter {id ->
            handleToDetail(id)
        }
        binding.listDataStories.adapter = mainAdapter
        showShimmerEffect()

        mainAdapter.addLoadStateListener { loadState ->
            if ((loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && mainAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                //showShimmerEffect()
                hideShimmerEffect()
                binding.swipeRefreshMain.isRefreshing = false
                binding.imgError.visibility = View.VISIBLE
                binding.txtError.visibility = View.VISIBLE
                binding.btnRetryStory.visibility = View.VISIBLE
                binding.btnRetryStory.isClickable = true

            }else{
                hideShimmerEffect()
                binding.swipeRefreshMain.isRefreshing = false
                binding.imgError.visibility = View.GONE
                binding.txtError.visibility = View.GONE
                binding.btnRetryStory.visibility = View.GONE
                binding.btnRetryStory.isClickable = false

            }

            binding.swipeRefreshMain.isRefreshing = loadState.source.refresh is LoadState.Loading

        }


        try {
            binding.listDataStories.apply {
                adapter = mainAdapter.withLoadStateFooter(
                    footer = LoadingStatePageAdapter{
                        mainAdapter.retry()
                    }
                )
            }
        }catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleToDetail(id: String?) {
        val toDetailStory = Intent(this@MainActivity,DetailStoryActivity::class.java)
        toDetailStory.putExtra(DETAIL_STORY,id)
        startActivity(toDetailStory)
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
            .setPositiveButton(getString(R.string.yes), object : DialogInterface.OnClickListener {
                override fun onClick(arg0: DialogInterface?, arg1: Int) {
                    super@MainActivity.onBackPressed()
                }
            }).create().show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout ->{
                viewModel.signOutUser()
                val intentToAuth = Intent(this,LoginRegisterActivity::class.java)
                intentToAuth.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentToAuth)
                finish()
                true
            }
            R.id.menu_setting ->{
                val intentSetting = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intentSetting)
                true
            }
            R.id.menu_maps ->{
                val toExploreMapPage =Intent(this,ExploreMapsActivity::class.java)
                startActivity(toExploreMapPage)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object{
        const val DETAIL_STORY="detail_story"
    }

}