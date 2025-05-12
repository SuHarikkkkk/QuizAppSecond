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
import com.google.firebase.auth.FirebaseAuth

class QuizSelectionFragment : Fragment() {

    private lateinit var binding: FragmentQuizSelectionBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedStandardQuiz: String? = null
    private var selectedUserQuiz: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizSelectionBinding.inflate(inflater, container, false)

        loadQuizzes("quizzes", binding.spinnerQuizzes) { selectedStandardQuiz = it }
        loadQuizzes("user_quizzes", binding.spinnerUserQuizzes) { selectedUserQuiz = it }

        binding.btnStartQuiz.setOnClickListener {
            val quizToStart = selectedStandardQuiz ?: selectedUserQuiz
            if (quizToStart != null) {
                val action = QuizSelectionFragmentDirections
                    .actionQuizSelectionFragmentToFragmentQuiz(quizToStart)
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Выберите квиз", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            (activity as MainActivity).logout()
        }

        binding.btnCreateQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_quizSelectionFragment_to_fragmentCreateQuiz)
        }

        binding.btnShareQuiz.setOnClickListener {
            selectedUserQuiz?.let { quizId ->
                val shareText = "Попробуй пройти мой квиз в приложении: \"$quizId\" 🎓\n" +
                        "Открой приложение и выбери его в разделе 'Пользовательские квизы'."

                val shareIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                startActivity(android.content.Intent.createChooser(shareIntent, "Поделиться квизом через:"))
            } ?: Toast.makeText(requireContext(), "Выберите пользовательский квиз для отправки", Toast.LENGTH_SHORT).show()
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
                    val name = document.getString("name") ?: document.id // fallback
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
