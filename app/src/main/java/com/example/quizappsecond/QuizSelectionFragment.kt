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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizSelectionFragment : Fragment() {

    private lateinit var binding: FragmentQuizSelectionBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedStandardQuizId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizSelectionBinding.inflate(inflater, container, false)

        // Загрузка квизов из коллекции "quizzes"
        loadQuizzes("quizzes", binding.spinnerQuizzes) { selectedStandardQuizId = it }

        binding.btnStartQuiz.setOnClickListener {
            selectedStandardQuizId?.let { quizId ->
                val action = QuizSelectionFragmentDirections
                    .actionQuizSelectionFragmentToFragmentQuiz(quizId, "quizzes")
                findNavController().navigate(action)
            } ?: Toast.makeText(requireContext(), "Выберите квиз", Toast.LENGTH_SHORT).show()
        }

        binding.btnYourQuizzes.setOnClickListener {
            findNavController().navigate(R.id.userQuizzesFragment)
        }

        binding.btnLogout.setOnClickListener {
            (activity as MainActivity).logout()
        }

        binding.btnCreateQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_quizSelectionFragment_to_fragmentCreateQuiz)
        }

        return binding.root
    }

    private fun loadQuizzes(
        collection: String,
        spinner: android.widget.Spinner,
        onSelected: (String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid

        val query = if (collection == "user_quizzes" && uid != null) {
            db.collection(collection).whereEqualTo("ownerUid", uid)
        } else {
            db.collection(collection)
        }

        query.get()
            .addOnSuccessListener { result ->
                val nameToIdMap = mutableMapOf<String, String>()
                val quizNames = mutableListOf<String>()

                for (document in result) {
                    val name = document.getString("name") ?: document.id
                    nameToIdMap[name] = document.id
                    quizNames.add(name)
                }

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    quizNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        val selectedName = parent?.getItemAtPosition(position) as? String
                        val selectedId = nameToIdMap[selectedName]
                        onSelected(selectedId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        onSelected(null)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Ошибка загрузки квизов: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
