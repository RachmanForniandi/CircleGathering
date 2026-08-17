package rachman.forniandi.circlegathering.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import rachman.forniandi.circlegathering.LoginRegister.LoginRegisterActivity
import rachman.forniandi.circlegathering.databinding.ActivityAboutUserBinding
import rachman.forniandi.circlegathering.viewModels.AboutUserViewModel

@AndroidEntryPoint
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

            btnSettingLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            btnLogout.setOnClickListener {
                viewModel.signOutUser()
                val intentToAuth = Intent(this@AboutUserActivity, LoginRegisterActivity::class.java)
                intentToAuth.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentToAuth)
                finish()
            }
        }
    }
}