package rachman.forniandi.circlegathering.LoginRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ActivityLoginRegisterBinding

@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //navController = findNavController(R.id.base_nav_host_fragment)
        //NavigationUI.setupWithNavController(navController)
        findNavController()

    }

    private fun findNavController():NavController? {
        val navHostFragment = (this.supportFragmentManager.findFragmentById(R.id.base_nav_host_fragment) as? NavHostFragment)
        return navHostFragment?.navController
    }
}