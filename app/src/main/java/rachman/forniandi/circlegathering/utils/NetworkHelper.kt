package rachman.forniandi.circlegathering.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Singleton

@Suppress("DEPRECATION")
@Singleton
class NetworkHelper (private val context: Context) {

    fun hasInternetConnectionForMain():Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities= connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)->true
            else -> false
        }
    }
}