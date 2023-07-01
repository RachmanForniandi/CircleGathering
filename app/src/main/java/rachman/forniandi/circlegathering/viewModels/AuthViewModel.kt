package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.models.login.ResponseLogin
import rachman.forniandi.circlegathering.models.register.ResponseRegister
import rachman.forniandi.circlegathering.repositories.AuthUserRepository
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.SessionPreferences
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthUserRepository,
    private val sessionPreferences: SessionPreferences,
    application: Application
): AndroidViewModel(application) {

    var networkStatus = false
    var backOnline = false

    var registerResponse:MutableLiveData<NetworkResult<ResponseRegister>> = MutableLiveData()
    var loginResponse:MutableLiveData<NetworkResult<ResponseLogin>> = MutableLiveData()


    fun actionRegister(name:String,email:String,password:String) = viewModelScope.launch {
        actionRegisterSafeCall(name, email, password)
    }

    fun actionLogin(email:String,password:String) = viewModelScope.launch {
        actionLoginSafeCall(email, password)
    }


    private suspend fun actionRegisterSafeCall(name:String,email:String,password:String){
        registerResponse.value = NetworkResult.Loading()
        if(hasInternetConnectionForAuth()){
            try {
                val registerFeedback = repository.remote.doRegister(name,email,password)
                registerResponse.value = handledRegisterResponse(registerFeedback)
            }catch (e: Exception){
                registerResponse.value = NetworkResult.Error("register failed.")
            }
        }else{
            registerResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handledRegisterResponse(response: Response<ResponseRegister>):NetworkResult<ResponseRegister>{
        when{
            response.message().toString().contains("timeout")->{
                return NetworkResult.Error("Timeout")
            }

            response.isSuccessful -> {
                val register = response.body()
                return NetworkResult.Success(register)
            }
            else->{
                return NetworkResult.Error(response.message())
            }
        }
    }

    private suspend fun actionLoginSafeCall(email:String,password:String){
        loginResponse.value = NetworkResult.Loading()
        if(hasInternetConnectionForAuth()){
            try {
                val loginFeedback = repository.remote.doLogin(email,password)
                loginResponse.value = handledLoginResponse(loginFeedback)

            }catch (e: Exception){
                loginResponse.value = NetworkResult.Error("Login Failed.")
            }
        }else{
            loginResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun handledLoginResponse(response: Response<ResponseLogin>): NetworkResult<ResponseLogin>? {
        when{
            response.message().toString().contains("timeout")->{
                return NetworkResult.Error("Timeout")
            }

            response.isSuccessful -> {
                val login = response.body()
                sessionPreferences.run {
                    login?.loginResult?.token?.let { saveTokenAuth(it) }
                    login?.loginResult?.name?.let{ saveUsername(it)}
                    setLoginUserStatus(true)
                }
                return NetworkResult.Success(login)
            }
            else->{
                return NetworkResult.Error(response.message())
            }
        }
    }

    /*fun actionSaveUserCredential(token: String) {
        viewModelScope.launch {
            repository.store.keepAuthToken(token)
        }
    }*/
    /*fun actionSaveAuthToken(token: String) {
        viewModelScope.launch {
            repository.store.keepAuthToken(token)
        }
    }*/

    /*fun actionSaveAuthUsername(name: String) {
        viewModelScope.launch {
            repository.store.keepDataUsername(name)
        }
    }*/

    private fun saveBackOnline(backOnline:Boolean)=
        viewModelScope.launch(Dispatchers.IO) {
            repository.online.saveBackOnline(backOnline)
        }

    private fun hasInternetConnectionForAuth():Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        )as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities= connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
            else -> false
        }
    }
    /*fun getLoginUserStatus() = sessionPreferences.getLoginUserStatus()

    suspend fun deleteCredentialUser(){
        sessionPreferences.run {
            deleteTokenAuth()
            setLoginUserStatus(false)
        }
    }*/

    fun showNetworkStatus(){
        if (!networkStatus){
            Toast.makeText(getApplication(),"No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        }else if (networkStatus){
            if (backOnline){
                Toast.makeText(getApplication(),"We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }



}