package com.example.quizappsecond

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quizappsecond.databinding.FragmentQuizBinding
import com.google.firebase.firestore.FirebaseFirestore

class FragmentQuiz : Fragment() {

    private lateinit var binding: FragmentQuizBinding
    private val db = FirebaseFirestore.getInstance()

    private var questions = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private lateinit var selectedQuiz: String
    private lateinit var collectionName: String

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


        loadQuestionsFromFirestore(selectedQuiz)

        return binding.root
    }

    private fun loadQuestionsFromFirestore(quizName: String) {
        db.collection(collectionName)
            .document(selectedQuiz)
            .get()
            .addOnSuccessListener { document ->
                val questionsData = document["questions"] as? List<Map<String, Any>>
                if (questionsData.isNullOrEmpty()) {
                    Toast.makeText(context, "No questions found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                questions.clear()
                for (q in questionsData) {
                    val text = q["question"] as? String ?: continue
                    val options = q["options"] as? List<String> ?: continue
                    val correctIndex = (q["correctIndex"] as? String)?.toIntOrNull() ?: continue
                    val correctAnswer = options.getOrNull(correctIndex) ?: continue
                    val imageUrl = q["imageUrl"] as? String

                    questions.add(Question(text, options, correctAnswer, imageUrl))
                }

                if (questions.isNotEmpty()) {
                    currentQuestionIndex = 0
                    correctAnswers = 0
                    showQuestion()
                } else {
                    Toast.makeText(context, "No valid questions", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error loading questions: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showQuestion() {
        val question = questions[currentQuestionIndex]
        binding.tvQuestion.text = question.text
        binding.rgOptions.clearCheck()

        val options = question.options
        binding.rbOption1.text = options.getOrNull(0) ?: ""
        binding.rbOption2.text = options.getOrNull(1) ?: ""
        binding.rbOption3.text = options.getOrNull(2) ?: ""
        binding.rbOption4.text = options.getOrNull(3) ?: ""

        // Загрузить изображение, если есть
        if (!question.imageUrl.isNullOrEmpty()) {
            binding.ivQuestionImage.visibility = View.VISIBLE
            Glide.with(this)
                .load(question.imageUrl)
                .into(binding.ivQuestionImage)
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
