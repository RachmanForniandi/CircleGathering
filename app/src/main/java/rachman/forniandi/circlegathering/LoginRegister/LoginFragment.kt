package rachman.forniandi.circlegathering.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.activities.MainActivity
import rachman.forniandi.circlegathering.activities.MainActivity.Companion.OBTAINED_TOKEN
import rachman.forniandi.circlegathering.activities.MainActivity.Companion.OBTAINED_USERNAME
import rachman.forniandi.circlegathering.databinding.FragmentLoginBinding
import rachman.forniandi.circlegathering.networkUtil.NetworkListener
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.animateLoadingProcessData
import rachman.forniandi.circlegathering.viewModels.AuthViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding :FragmentLoginBinding?= null

    private val loginViewModel: AuthViewModel by viewModels()
    private lateinit var networkListener: NetworkListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentLoginBinding.inflate(inflater, container, false)

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireActivity())
                .collect { status->
                    Log.d("NetworkListener",status.toString())
                    loginViewModel.networkStatus = status
                    loginViewModel.showNetworkStatus()
                }
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startActionLoginOrRegister()
    }

    private fun startActionLoginOrRegister() {

        binding?.btnLogin?.setOnClickListener {
                actionLogin()
        }

        binding?.txtLinkToRegister?.setOnClickListener {
                Navigation.createNavigateOnClickListener(R.id.action_LoginFragment_to_RegisterFragment)
            Toast.makeText(requireActivity(),"test klik register",Toast.LENGTH_SHORT).show()
        }

    }

    private fun actionLogin() {
        val email = binding?.etEmail?.text.toString().trim()
        val password = binding?.etPassword?.text.toString()

        lifecycleScope.launch{

            loginViewModel.actionLogin(email, password)
            loginViewModel.loginResponse.observe(viewLifecycleOwner,{
                    response->
                when(response){
                    is NetworkResult.Success->{
                        applyLoadProgressStateLogin(false)
                        val responseLogin =response.data
                        val bundleLogin = Bundle()
                        responseLogin?.loginResult?.token.let { token->
                            token?.let { loginViewModel.actionSaveAuthToken(it) }
                            bundleLogin.putString(OBTAINED_TOKEN,token)
                        }
                        responseLogin?.loginResult?.name.let {
                                name->
                            name?.let { loginViewModel.actionSaveAuthUsername(it) }
                            bundleLogin.putString(OBTAINED_USERNAME,name)
                        }

                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        intent.putExtras(bundleLogin)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is NetworkResult.Error->{
                        applyLoadProgressStateLogin(false)
                        Toast.makeText(requireContext(),
                            response.message.toString()
                            , Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Loading->{
                        applyLoadProgressStateLogin(true)
                    }
                }
            })
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun applyLoadProgressStateLogin(onProcess:Boolean){
        binding?.etEmail?.isEnabled = !onProcess
        binding?.etPassword?.isEnabled = !onProcess
        binding?.btnLogin?.isEnabled = !onProcess

        if (onProcess){
            binding?.pgLogin?.animateLoadingProcessData(true)
        }else{
            binding?.pgLogin?.animateLoadingProcessData(false)
        }
    }
}