package com.example.quizappsecond.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    sealed class AuthResult {
        object Success : AuthResult()
        data class Failure(val message: String) : AuthResult()
        object None : AuthResult()
    }

    private val _authResult = MutableLiveData<AuthResult>(AuthResult.None)
    val authResult: LiveData<AuthResult> get() = _authResult

    fun register(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authResult.value = AuthResult.Failure("Email and password must not be empty")
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _authResult.value = if (task.isSuccessful) {
                    AuthResult.Success
                } else {
                    AuthResult.Failure(task.exception?.message ?: "Unknown error")
                }
            }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authResult.value = AuthResult.Failure("Email and password must not be empty")
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _authResult.value = if (task.isSuccessful) {
                    AuthResult.Success
                } else {
                    AuthResult.Failure(task.exception?.message ?: "Unknown error")
                }
            }
    }
}
