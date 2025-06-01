package com.example.quizappsecond

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.quizappsecond.databinding.FragmentCreateQuizBinding
import com.example.quizappsecond.viewmodel.CreateQuizViewModel

class FragmentCreateQuiz : Fragment() {

    private lateinit var binding: FragmentCreateQuizBinding
    private val viewModel: CreateQuizViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreateQuizBinding.inflate(inflater, container, false)

        binding.btnAddQuestion.setOnClickListener {
            val question = getQuestionFromInputs()
            if (question != null) {
                viewModel.addQuestion(question)
                Toast.makeText(context, "Вопрос добавлен", Toast.LENGTH_SHORT).show()
                clearInputs()
            }
        }

        binding.btnSaveQuiz.setOnClickListener {
            viewModel.saveQuiz(binding.etQuizTitle.text.toString().trim())
        }

        viewModel.quizSaved.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "Квиз сохранён", Toast.LENGTH_SHORT).show()
                clearInputs()
                binding.etQuizTitle.text?.clear()
                viewModel.clearQuestions()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun getQuestionFromInputs(): Question? {
        val text = binding.etQuestionText.text.toString().trim()
        val options = listOf(
            binding.etOption1.text.toString(),
            binding.etOption2.text.toString(),
            binding.etOption3.text.toString(),
            binding.etOption4.text.toString()
        )
        val correctIndex = binding.etCorrectIndex.text.toString().toIntOrNull()
        val imageUrl = binding.etImageUrl.text.toString().trim()

        if (text.isBlank() || options.any { it.isBlank() } || correctIndex !in 0..3) {
            Toast.makeText(context, "Заполните все поля правильно", Toast.LENGTH_SHORT).show()
            return null
        }

        return Question(text, options, options[correctIndex!!], imageUrl.takeIf { it.isNotBlank() })
    }

    private fun clearInputs() {
        binding.etQuestionText.text?.clear()
        binding.etOption1.text?.clear()
        binding.etOption2.text?.clear()
        binding.etOption3.text?.clear()
        binding.etOption4.text?.clear()
        binding.etCorrectIndex.text?.clear()
        binding.etImageUrl.text?.clear()
    }
}

