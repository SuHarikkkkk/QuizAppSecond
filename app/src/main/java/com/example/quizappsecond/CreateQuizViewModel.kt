package com.example.quizappsecond.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizappsecond.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateQuizViewModel : ViewModel() {

    private val _questions = MutableLiveData<MutableList<Question>>(mutableListOf())
    val questions: LiveData<MutableList<Question>> = _questions

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val quizSaved = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun addQuestion(question: Question) {
        _questions.value?.add(question)
        _questions.value = _questions.value
    }

    fun saveQuiz(title: String) {
        val currentUser = auth.currentUser
        if (title.isBlank() || _questions.value.isNullOrEmpty() || currentUser == null) {
            errorMessage.value = "Введите название квиза и хотя бы один вопрос"
            return
        }

        val questionsData = _questions.value!!.map {
            mapOf(
                "question" to it.text,
                "options" to it.options,
                "correctIndex" to it.options.indexOf(it.correctAnswer).toString(),
                "imageUrl" to it.imageUrl
            )
        }

        val quizData = mapOf(
            "name" to title,
            "ownerUid" to currentUser.uid,
            "questions" to questionsData
        )

        db.collection("user_quizzes").document(title)
            .set(quizData)
            .addOnSuccessListener { quizSaved.value = true }
            .addOnFailureListener { errorMessage.value = "Ошибка: ${it.message}" }
    }

    fun clearQuestions() {
        _questions.value = mutableListOf()
    }
}
