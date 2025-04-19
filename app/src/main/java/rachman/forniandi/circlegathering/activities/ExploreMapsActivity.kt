package rachman.forniandi.circlegathering.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ActivityExploreMapsBinding
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.animateLoadingProcessData
import rachman.forniandi.circlegathering.viewModels.MapsLocationViewModel

@AndroidEntryPoint
class ExploreMapsActivity : AppCompatActivity(), OnMapReadyCallback  {

    private lateinit var mapPage: GoogleMap
    private lateinit var binding: ActivityExploreMapsBinding
    private val viewModel: MapsLocationViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = (this.supportFragmentManager.findFragmentById(R.id.maps_nav_host_fragment) as? SupportMapFragment)

        mapFragment?.getMapAsync(this@ExploreMapsActivity)

    }
    

    override fun onMapReady(gMap: GoogleMap) {
        mapPage = gMap
        mapPage.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@ExploreMapsActivity, R.raw.map_style))
        showPinLocation()
    }

    private fun showPinLocation() {
        viewModel.doShowAllStoriesLocationData()
        viewModel.getAllStoriesLocationResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    applyLoadProgressStateMap(false)
                    response.data?.listStory?.forEach { storyItem ->
                        val positionUser = storyItem.lat?.let { storyItem.lon?.let { it1 ->
                            LatLng(it,
                                it1
                            )
                        }}
                        mapPage.addMarker(MarkerOptions().position(positionUser!!).title(storyItem.name))
                        mapPage.animateCamera(CameraUpdateFactory.newLatLngZoom(positionUser, 5f))
                    }
                    Log.e("ExploreMapsActivity","Network Success called")
                }

                is NetworkResult.Error -> {
                    applyLoadProgressStateMap(false)
                    Snackbar.make(
                        binding.root,
                        response.message.toString(), Snackbar.LENGTH_SHORT).show()
                    Log.e("ExploreMapsActivity","Network Error called")
                }

                is NetworkResult.Loading -> {
                    applyLoadProgressStateMap(true)
                    Log.e("ExploreMapsActivity","Network Loading called")
                }
            }
        }
        
    }

    override fun onResume() {
        super.onResume()
    }

    private fun applyLoadProgressStateMap(onProcess:Boolean){

        if (onProcess){
            binding.maskedViewPgMap.animateLoadingProcessData(true)
        }else{
            binding.maskedViewPgMap.animateLoadingProcessData(false)
        }
    }
}