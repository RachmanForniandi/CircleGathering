package rachman.forniandi.circlegathering.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ActivityExploreMapsBinding
import rachman.forniandi.circlegathering.databinding.ActivityLoginRegisterBinding

@AndroidEntryPoint
class ExploreMapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExploreMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findMapNavController()

    }

    private fun findMapNavController(): NavController?  {
        val navHostFragment = (this.supportFragmentManager.findFragmentById(R.id.maps_nav_host_fragment) as? NavHostFragment)
        return navHostFragment?.navController
    }
}