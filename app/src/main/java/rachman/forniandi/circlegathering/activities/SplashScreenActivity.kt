package rachman.forniandi.circlegathering.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.LoginRegister.LoginRegisterActivity
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.viewModels.SplashScreenViewModel
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Timer("splashGone", true).schedule(3000) {
            chooseUserDirection()
        }
    }

    private fun chooseUserDirection() {
        lifecycleScope.launch {
            viewModel.checkUserStatus().observe(this@SplashScreenActivity) { userStatus->
                if (userStatus){
                    Intent(this@SplashScreenActivity, MainActivity::class.java).also { backToMain->
                        //backToMain.putExtra(OBTAINED_TOKEN,sessionToken)
                        //backToMain.putExtra(OBTAINED_USERNAME,username)
                        startActivity(backToMain)
                        finish()
                    }
                }else{

                    val intentFromBeginning = Intent(this@SplashScreenActivity,LoginRegisterActivity::class.java)
                    startActivity(intentFromBeginning)
                    finish()
                }

            }


        }
    }

}