package com.example.quizappsecond

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizappsecond.databinding.FragmentUserQuizzesBinding
import com.example.quizappsecond.viewmodel.QuizViewModel

class UserQuizzesFragment : Fragment() {

    private lateinit var binding: FragmentUserQuizzesBinding
    private val quizViewModel: QuizViewModel by viewModels()
    private var selectedUserQuiz: String? = null
    private var nameToIdMap: Map<String, String> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserQuizzesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizViewModel.loadUserQuizzes()

        quizViewModel.userQuizzes.observe(viewLifecycleOwner) { quizzes ->
            nameToIdMap = quizzes
            val quizNames = quizzes.keys.toList()

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                quizNames
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.spinnerUserQuizzes.adapter = adapter
        }

        binding.spinnerUserQuizzes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val name = parent?.getItemAtPosition(position) as? String
                selectedUserQuiz = nameToIdMap[name]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedUserQuiz = null
            }
        }

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
    }
}
