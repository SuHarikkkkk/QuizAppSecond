package com.example.quizappsecond

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quizappsecond.databinding.FragmentUserQuizzesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserQuizzesFragment : Fragment() {

    private lateinit var binding: FragmentUserQuizzesBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedUserQuiz: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserQuizzesBinding.inflate(inflater, container, false)

        loadUserQuizzes()

        binding.btnStartUserQuiz.setOnClickListener {
            selectedUserQuiz?.let {
                val action = UserQuizzesFragmentDirections
                    .actionUserQuizzesFragmentToFragmentQuiz(it, "user_quizzes")
                findNavController().navigate(action)
            } ?: Toast.makeText(requireContext(), "Выберите квиз", Toast.LENGTH_SHORT).show()
        }

        binding.btnShareQuiz.setOnClickListener {
            selectedUserQuiz?.let { quizId ->
                val shareText = "Пройди мой квиз: $quizId в приложении QuizApp!"
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, "Поделиться квизом"))
            } ?: Toast.makeText(requireContext(), "Сначала выберите квиз", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun loadUserQuizzes() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("user_quizzes").whereEqualTo("ownerUid", uid).get()
            .addOnSuccessListener { result ->
                val quizNames = mutableListOf<String>()
                val nameToIdMap = mutableMapOf<String, String>()

                for (doc in result) {
                    val name = doc.getString("name") ?: doc.id
                    nameToIdMap[name] = doc.id
                    quizNames.add(name)
                }

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    quizNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerUserQuizzes.adapter = adapter

                binding.spinnerUserQuizzes.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                        ) {
                            val selectedName = parent?.getItemAtPosition(position) as? String
                            selectedUserQuiz = nameToIdMap[selectedName]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            selectedUserQuiz = null
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Ошибка загрузки: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
