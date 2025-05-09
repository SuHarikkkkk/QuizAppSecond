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
    private var selectedQuizId: String? = null
    private val quizIdMap = mutableMapOf<String, String>() // name -> id

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizSelectionBinding.inflate(inflater, container, false)

        db.collection("quizzes").get()
            .addOnSuccessListener { result ->
                val quizNames = mutableListOf<String>()
                for (document in result) {
                    val quizId = document.id
                    val quizName = document.getString("name") ?: quizId
                    quizNames.add(quizName)
                    quizIdMap[quizName] = quizId
                }

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    quizNames
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
                val selectedName = parent?.getItemAtPosition(position) as? String
                selectedQuizId = quizIdMap[selectedName]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedQuizId = null
            }
        }

        binding.btnStartQuiz.setOnClickListener {
            selectedQuizId?.let { quizId ->
                val action = QuizSelectionFragmentDirections
                    .actionQuizSelectionFragmentToFragmentQuiz(quizId)
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
