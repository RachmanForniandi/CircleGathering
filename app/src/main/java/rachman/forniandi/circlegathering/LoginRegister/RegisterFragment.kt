package rachman.forniandi.circlegathering.LoginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.FragmentRegisterBinding
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.animateLoadingProcessData
import rachman.forniandi.circlegathering.viewModels.AuthViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private val registerViewModel:AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionInRegisterPage()
    }

    private fun setActionInRegisterPage() {
        binding?.btnRegister?.setOnClickListener {
                actionRegister()
        }

    }

    private fun actionRegister() {
        val username = binding?.etUsername?.text.toString().trim()
        val email = binding?.etEmail?.text.toString().trim()
        val password = binding?.etPassword?.text.toString()

        lifecycleScope.launch {
            registerViewModel.actionRegister(username,email, password)
            registerViewModel.registerResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        applyLoadProgressStateRegister(false)
                        val responseRegister = response.data
                        Toast.makeText(
                            requireActivity(),
                            responseRegister?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)

                    }

                    is NetworkResult.Error -> {
                        Toast.makeText(
                            requireContext(),
                            response.message.toString(), Toast.LENGTH_SHORT
                        ).show()
                        applyLoadProgressStateRegister(false)
                    }

                    is NetworkResult.Loading -> {
                        applyLoadProgressStateRegister(true)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun applyLoadProgressStateRegister(onProcess:Boolean){
        binding?.etUsername?.isEnabled = !onProcess
        binding?.etEmail?.isEnabled = !onProcess
        binding?.etPassword?.isEnabled = !onProcess
        binding?.btnRegister?.isEnabled = !onProcess

        if (onProcess){
            binding?.maskedViewPgRegister?.animateLoadingProcessData(true)
        }else{
            binding?.maskedViewPgRegister?.animateLoadingProcessData(false)
        }
    }
}