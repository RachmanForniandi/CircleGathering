package rachman.forniandi.circlegathering.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ActivityAboutUserBinding
import rachman.forniandi.circlegathering.databinding.ActivityMainBinding
import rachman.forniandi.circlegathering.viewModels.AboutUserViewModel
import rachman.forniandi.circlegathering.viewModels.MainViewModel
import kotlin.getValue

class AboutUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUserBinding
    private val viewModel: AboutUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeUserData()
    }

    private fun observeUserData() {
        binding.apply {
            viewModel.getUserName().observe(this@AboutUserActivity) { user ->
                binding.txtUsername.text = user
            }

            viewModel.getUserId().observe(this@AboutUserActivity) { id ->
                binding.txtUserid.text = id
            }
        }
    }
}