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
import rachman.forniandi.circlegathering.activities.MainActivity.Companion.OBTAINED_TOKEN
import rachman.forniandi.circlegathering.activities.MainActivity.Companion.OBTAINED_USERNAME
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
            viewModel.checkSessionToken().collect(){ sessionToken->
                if (sessionToken.isNullOrEmpty()){
                    val intentFromBeginning = Intent(this@SplashScreenActivity,LoginRegisterActivity::class.java)
                    startActivity(intentFromBeginning)
                    finish()
                }else{
                    Intent(this@SplashScreenActivity, MainActivity::class.java).also { backToMain->
                        backToMain.putExtra(OBTAINED_TOKEN,sessionToken)
                        //backToMain.putExtra(OBTAINED_USERNAME,username)
                        startActivity(backToMain)
                        finish()
                    }

                }

            }


        }
    }

}