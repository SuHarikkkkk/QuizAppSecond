package com.example.quizappsecond

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.quizappsecond.databinding.FragmentLoginBinding
import com.example.quizappsecond.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Наблюдение за результатом авторизации
        viewModel.authResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is AuthViewModel.AuthResult.Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_quizSelectionFragment)
                }
                is AuthViewModel.AuthResult.Failure -> {
                    Toast.makeText(requireContext(), "Login failed: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        })

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }
}
