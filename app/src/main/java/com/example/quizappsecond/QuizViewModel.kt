package com.example.quizappsecond.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizappsecond.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _createResult = MutableLiveData<Boolean>()
    val createResult: LiveData<Boolean> = _createResult

    private val _userQuizzes = MutableLiveData<Map<String, String>>()
    val userQuizzes: LiveData<Map<String, String>> = _userQuizzes
    val questions = MutableLiveData<List<Question>>()
    val loadingError = MutableLiveData<String?>()

    fun loadQuestions(collection: String, quizId: String) {
        db.collection(collection)
            .document(quizId)
            .get()
            .addOnSuccessListener { document ->
                val questionsData = document["questions"] as? List<Map<String, Any>>
                if (questionsData.isNullOrEmpty()) {
                    loadingError.postValue("Вопросы не найдены")
                    return@addOnSuccessListener
                }

                val parsedQuestions = questionsData.mapNotNull { q ->
                    val text = q["question"] as? String ?: return@mapNotNull null
                    val options = q["options"] as? List<String> ?: return@mapNotNull null
                    val correctIndex = (q["correctIndex"] as? String)?.toIntOrNull() ?: return@mapNotNull null
                    val correctAnswer = options.getOrNull(correctIndex) ?: return@mapNotNull null
                    val imageUrl = q["imageUrl"] as? String

                    Question(text, options, correctAnswer, imageUrl)
                }

                questions.postValue(parsedQuestions)
            }
            .addOnFailureListener {
                loadingError.postValue("Ошибка загрузки: ${it.message}")
            }
    }

    fun createQuiz(name: String, questions: List<Map<String, Any>>) {
        val uid = auth.currentUser?.uid ?: return
        val quiz = hashMapOf(
            "name" to name,
            "ownerUid" to uid,
            "questions" to questions
        )
        db.collection("user_quizzes")
            .add(quiz)
            .addOnSuccessListener {
                _createResult.value = true
            }
            .addOnFailureListener {
                _createResult.value = false
            }
    }

    fun loadUserQuizzes() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("user_quizzes")
            .whereEqualTo("ownerUid", uid)
            .get()
            .addOnSuccessListener { result ->
                val map = mutableMapOf<String, String>()
                for (doc in result) {
                    val name = doc.getString("name") ?: doc.id
                    map[name] = doc.id
                }
                _userQuizzes.value = map
            }
            .addOnFailureListener {
                _userQuizzes.value = emptyMap()
            }
    }
}