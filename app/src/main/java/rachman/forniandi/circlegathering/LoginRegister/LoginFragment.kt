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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.activities.MainActivity
import rachman.forniandi.circlegathering.databinding.FragmentLoginBinding
import rachman.forniandi.circlegathering.networkUtil.NetworkListener
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.animateLoadingProcessData
import rachman.forniandi.circlegathering.viewModels.AuthViewModel


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: AuthViewModel by viewModels()
    private lateinit var networkListener: NetworkListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentLoginBinding.inflate(inflater, container, false)


        lifecycleScope.launch{
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireActivity())
                .collect { status->
                    Log.d("NetworkListener",status.toString())
                    loginViewModel.networkStatus = status
                    loginViewModel.showNetworkStatus()
                }
        }
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startActionLoginOrRegister()
    }

    private fun startActionLoginOrRegister() {

        binding.btnLogin.setOnClickListener {
                actionLogin()
        }

        binding.txtLinkToRegister.setOnClickListener (Navigation.createNavigateOnClickListener(R.id.action_LoginFragment_to_RegisterFragment))
    }

    private fun actionLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        viewLifecycleOwner.lifecycleScope.launch{

            loginViewModel.actionLogin(email, password)
            loginViewModel.loginResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Loading -> {
                        applyLoadProgressStateLogin(true)
                    }

                    is NetworkResult.Success -> {
                        applyLoadProgressStateLogin(false)
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }

                    is NetworkResult.Error -> {
                        applyLoadProgressStateLogin(false)
                        Snackbar.make(
                            binding.root,
                            response.message.toString(), Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyLoadProgressStateLogin(onProcess:Boolean){
        binding.etEmail.isEnabled = !onProcess
        binding.etPassword.isEnabled = !onProcess
        binding.btnLogin.isEnabled = !onProcess

        if (onProcess){
            binding.maskedViewPgLogin.animateLoadingProcessData(true)
        }else{
            binding.maskedViewPgLogin.animateLoadingProcessData(false)
        }
    }


}