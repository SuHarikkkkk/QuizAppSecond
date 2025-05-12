package com.example.quizappsecond.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.quizappsecond.R
import com.example.quizappsecond.databinding.FragmentLoginIntroBinding


class LoginIntroFragment : Fragment() {

    private lateinit var binding: FragmentLoginIntroBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginIntroBinding.inflate(inflater, container, false)

        binding.btnGetStarted.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginIntroFragment_to_loginFragment)
        }

        return binding.root
    }
}