package com.example.quizappsecond

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quizappsecond.databinding.FragmentCreateQuizBinding
import com.google.firebase.firestore.FirebaseFirestore

class FragmentCreateQuiz : Fragment() {

    private lateinit var binding: FragmentCreateQuizBinding
    private val questions = mutableListOf<Question>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateQuizBinding.inflate(inflater, container, false)

        binding.btnAddQuestion.setOnClickListener {
            addQuestion()
        }

        binding.btnSaveQuiz.setOnClickListener {
            saveQuizToFirestore()
        }

        return binding.root
    }

    private fun addQuestion() {
        val questionText = binding.etQuestionText.text.toString().trim()
        val options = listOf(
            binding.etOption1.text.toString().trim(),
            binding.etOption2.text.toString().trim(),
            binding.etOption3.text.toString().trim(),
            binding.etOption4.text.toString().trim()
        )
        val correctIndex = binding.etCorrectIndex.text.toString().toIntOrNull()
        val imageUrl = binding.etImageUrl.text.toString().trim()

        if (questionText.isBlank() || options.any { it.isBlank() } || correctIndex !in 0..3) {
            Toast.makeText(requireContext(), "Заполните вопрос и 4 варианта. Правильный индекс 0–3", Toast.LENGTH_SHORT).show()
            return
        }

        val question = Question(
            text = questionText,
            options = options,
            correctAnswer = options[correctIndex!!],
            imageUrl = if (imageUrl.isNotBlank()) imageUrl else null
        )

        questions.add(question)
        Toast.makeText(requireContext(), "Вопрос добавлен (${questions.size})", Toast.LENGTH_SHORT).show()
        clearQuestionFields()
    }

    private fun saveQuizToFirestore() {
        val quizTitle = binding.etQuizTitle.text.toString().trim()

        if (quizTitle.isEmpty() || questions.isEmpty()) {
            Toast.makeText(requireContext(), "Введите название квиза и хотя бы один вопрос", Toast.LENGTH_SHORT).show()
            return
        }

        val questionsData = questions.map {
            mapOf(
                "question" to it.text,
                "options" to it.options,
                "correctIndex" to it.options.indexOf(it.correctAnswer).toString(),
                "imageUrl" to it.imageUrl
            )
        }

        db.collection("quizzes").document(quizTitle)
            .set(mapOf("questions" to questionsData))
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Квиз \"$quizTitle\" сохранён!", Toast.LENGTH_LONG).show()
                clearAll()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Ошибка сохранения: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearQuestionFields() {
        binding.etQuestionText.text?.clear()
        binding.etOption1.text?.clear()
        binding.etOption2.text?.clear()
        binding.etOption3.text?.clear()
        binding.etOption4.text?.clear()
        binding.etCorrectIndex.text?.clear()
        binding.etImageUrl.text?.clear()
    }

    private fun clearAll() {
        clearQuestionFields()
        binding.etQuizTitle.text?.clear()
        questions.clear()
    }
}
