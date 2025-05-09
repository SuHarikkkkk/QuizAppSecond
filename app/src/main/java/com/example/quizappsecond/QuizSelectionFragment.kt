package com.example.quizappsecond

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizappsecond.databinding.FragmentQuizSelectionBinding
import com.google.firebase.firestore.FirebaseFirestore

class QuizSelectionFragment : Fragment() {

    private lateinit var binding: FragmentQuizSelectionBinding
    private val db = FirebaseFirestore.getInstance()
    private var selectedQuizName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizSelectionBinding.inflate(inflater, container, false)

        // Загружаем квизы
        db.collection("quizzes").get()
            .addOnSuccessListener { result ->
                val quizList = mutableListOf<String>()
                for (document in result) {
                    val quizName = document.id
                    quizList.add(quizName)
                }

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    quizList
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerQuizzes.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    activity,
                    "Failed to load quizzes: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        binding.spinnerQuizzes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedQuizName = parent?.getItemAtPosition(position) as? String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedQuizName = null
            }
        }

        binding.btnStartQuiz.setOnClickListener {
            selectedQuizName?.let { quizName ->
                val action = QuizSelectionFragmentDirections
                    .actionQuizSelectionFragmentToFragmentQuiz(quizName)
                findNavController().navigate(action)
            } ?: Toast.makeText(
                requireContext(),
                "Please select a quiz",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnLogout.setOnClickListener {
            (activity as MainActivity).logout()
        }

        return binding.root
    }
}
