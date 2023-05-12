package rachman.forniandi.circlegathering.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import rachman.forniandi.circlegathering.LoginRegister.LoginRegisterActivity
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.adapters.MainAdapter
import rachman.forniandi.circlegathering.databinding.ActivityMainBinding
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.viewModels.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter by lazy { MainAdapter() }
    private var token: String = ""
    private var username: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityMainBinding.inflate(layoutInflater)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val extras = intent.extras

        token = extras?.getString(OBTAINED_TOKEN)!!
        username = extras.getString(OBTAINED_USERNAME)!!

        binding.txtUsername.text = username

        setSwipeRefreshAtMainPage()
        showDataStoriesOnMain()
        requestDataRemoteStories()
        binding.fabAddStory.setOnClickListener { view ->

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
        viewModel.doShowAllStoriesData(token)
        viewModel.getAllStoriesResponse.observe(this, {
                response->
            when(response){
                is NetworkResult.Success->{
                    hideShimmerEffect()
                    response.data?.let { adapter.setData(it) }
                    binding.swipeRefreshMain.isRefreshing = false
                }
                is NetworkResult.Error->{
                    hideShimmerEffect()
                    Toast.makeText(this,
                        response.message.toString()
                        , Toast.LENGTH_SHORT).show()
                    binding.swipeRefreshMain.isRefreshing = false
                }
                is NetworkResult.Loading->{
                    showShimmerEffect()
                }
            }
        })
    }

    private fun showDataStoriesOnMain() {
        binding.listDataStories.adapter = adapter
        showShimmerEffect()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout ->{
                viewModel.clearTheTokenAndSession("")
                viewModel.actionClearDataUserName()
                val intentToAuth = Intent(this,LoginRegisterActivity::class.java)
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


    companion object {
        const val OBTAINED_TOKEN = "obtained_token"
        const val OBTAINED_USERNAME="obtained_username"
    }
}