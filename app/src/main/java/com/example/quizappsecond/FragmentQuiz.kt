package com.example.quizappsecond

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.quizappsecond.databinding.FragmentQuizBinding
import com.example.quizappsecond.viewmodel.QuizViewModel

class FragmentQuiz : Fragment() {

    private lateinit var binding: FragmentQuizBinding
    private val quizViewModel: QuizViewModel by viewModels()

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var selectedQuiz = ""
    private var collectionName = ""
    private var questions: List<Question> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = FragmentQuizArgs.fromBundle(requireArguments())
        selectedQuiz = args.quizId
        collectionName = args.collection
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        setupListeners()
        observeViewModel()

        quizViewModel.loadQuestions(collectionName, selectedQuiz)

        return binding.root
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
            val selectedAnswer = getSelectedAnswer()
            if (selectedAnswer == questions[currentQuestionIndex].correctAnswer) {
                correctAnswers++
            }
            currentQuestionIndex++
            if (currentQuestionIndex < questions.size) {
                showQuestion()
            } else {
                (activity as MainActivity).showResults(correctAnswers, questions.size)
            }
        }

        binding.btnPrevious.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                showQuestion()
            } else {
                Toast.makeText(requireContext(), "Это первый вопрос", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        quizViewModel.questions.observe(viewLifecycleOwner) { loadedQuestions ->
            questions = loadedQuestions
            currentQuestionIndex = 0
            correctAnswers = 0
            showQuestion()
        }

        quizViewModel.loadingError.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showQuestion() {
        if (questions.isEmpty() || currentQuestionIndex >= questions.size) return

        val question = questions[currentQuestionIndex]
        binding.tvQuestion.text = question.text
        binding.rgOptions.clearCheck()

        val options = question.options
        binding.rbOption1.text = options.getOrNull(0) ?: ""
        binding.rbOption2.text = options.getOrNull(1) ?: ""
        binding.rbOption3.text = options.getOrNull(2) ?: ""
        binding.rbOption4.text = options.getOrNull(3) ?: ""

        if (!question.imageUrl.isNullOrEmpty()) {
            binding.ivQuestionImage.visibility = View.VISIBLE
            Glide.with(this).load(question.imageUrl).into(binding.ivQuestionImage)
        } else {
            binding.ivQuestionImage.visibility = View.GONE
        }
    }

    private fun getSelectedAnswer(): String {
        return when (binding.rgOptions.checkedRadioButtonId) {
            binding.rbOption1.id -> binding.rbOption1.text.toString()
            binding.rbOption2.id -> binding.rbOption2.text.toString()
            binding.rbOption3.id -> binding.rbOption3.text.toString()
            binding.rbOption4.id -> binding.rbOption4.text.toString()
            else -> ""
        }
    }
}
