package com.example.quizappsecond

data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswer: String,
    val imageUrl: String? = null
)
