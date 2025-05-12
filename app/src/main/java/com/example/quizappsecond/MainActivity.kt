package com.example.quizappsecond

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.quizappsecond.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем NavHostFragment и настраиваем NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Привязываем NavController к ActionBar для поддержки навигации
        NavigationUI.setupActionBarWithNavController(this, navController)

    }

    // Обработка кнопки "Назад"
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun showResults(correct: Int, total: Int) {
        val action = FragmentQuizDirections.actionFragmentQuizToResultFragment(correct, total)
        navController.navigate(action)
    }

    // Функция для выхода из аккаунта
    fun logout() {
        firebaseAuth.signOut()
        navController.navigate(R.id.loginFragment)
    }
}
