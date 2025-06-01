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
import com.example.quizappsecond.databinding.FragmentRegisterBinding
import com.example.quizappsecond.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.register(email, password)
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.authResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is AuthViewModel.AuthResult.Success -> {
                    Toast.makeText(activity, "Registration successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is AuthViewModel.AuthResult.Failure -> {
                    Toast.makeText(activity, "Registration failed: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        })
    }
}
