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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.LoginRegister.LoginRegisterActivity
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.adapters.MainAdapter
import rachman.forniandi.circlegathering.databinding.ActivityMainBinding
import rachman.forniandi.circlegathering.models.allStories.ListStoryItem
import rachman.forniandi.circlegathering.models.allStories.ResponseAllStories
import rachman.forniandi.circlegathering.utils.NetworkListener
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.viewModels.MainViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var dataRequested = false
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var mainAdapter :MainAdapter
    private lateinit var networkListener: NetworkListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityMainBinding.inflate(layoutInflater)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.lifecycleOwner = this
        binding.mainViewModel= viewModel

        setUserName()
        showDataStoriesOnMain()
        setSwipeRefreshAtMainPage()
        requestDataRemoteStories()
        binding.fabAddStory.setOnClickListener {
            val intentToAddData = Intent(this,FormAddDataActivity::class.java)
            startActivity(intentToAddData)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                networkListener = NetworkListener()
                networkListener.checkNetworkAvailability(this@MainActivity)
                    .collect { status->
                        Log.d("NetworkListener",status.toString())
                        viewModel.networkStatus = status
                        viewModel.showNetworkStatus()
                        readDbLocalStories()
                    }
            }
        }

    }

    private fun setUserName() {
        viewModel.getUserName().observe(this) { user ->
            binding.txtUsername.text = user
        }
    }


    private fun setSwipeRefreshAtMainPage() {
        binding.swipeRefreshMain.setOnRefreshListener {
            requestDataRemoteStories()
            hideShimmerEffect()
        }
    }

    private fun requestDataRemoteStories() {
        binding.swipeRefreshMain.isRefreshing = true
        viewModel.doShowAllStoriesData()
        viewModel.getAllStoriesResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mainAdapter.setData(it) }
                    binding.swipeRefreshMain.isRefreshing = false
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(
                        this,
                        response.message.toString(), Toast.LENGTH_SHORT
                    ).show()
                    binding.swipeRefreshMain.isRefreshing = false
                }

                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun showDataStoriesOnMain() {
        mainAdapter = MainAdapter(arrayListOf())
        mainAdapter.setOnClickListener(object :MainAdapter.OnStoryClickListener{
            override fun onClick(position: Int, story: ListStoryItem) {
                val toDetailStory = Intent(this@MainActivity,DetailStoryActivity::class.java)
                toDetailStory.putExtra(DETAIL_STORY,story)
                startActivity(toDetailStory)
            }
        })
        binding.listDataStories.adapter = mainAdapter
        showShimmerEffect()
    }

    private fun readDbLocalStories(){
        lifecycleScope.launch {
            viewModel.readStoriesLocal.observe(this@MainActivity){ db ->
                if (db.isNotEmpty() && dataRequested){
                    mainAdapter.setData(db.first().listStoryItem as ResponseAllStories)
                    hideShimmerEffect()
                }else{
                    if (!dataRequested){
                        requestDataRemoteStories()
                    }
                }
            }
        }
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object{
        const val DETAIL_STORY="detail_story"
    }

}