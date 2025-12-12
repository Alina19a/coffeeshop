package com.example.coffeeshop

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeshop.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupToolbar()
        loadUserData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Профиль"
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        user?.let {
            binding.tvUserName.text = it.displayName ?: "Пользователь"
            binding.tvUserEmail.text = it.email ?: "Email не указан"

            // Можно добавить дополнительные данные
            val registrationDate = it.metadata?.creationTimestamp
            registrationDate?.let { timestamp ->
                // Конвертируем timestamp в дату
                // binding.tvRegistrationDate.text = SimpleDateFormat("dd.MM.yyyy").format(Date(timestamp))
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Редактирование профиля", Toast.LENGTH_SHORT).show()
            // Здесь можно открыть экран редактирования профиля
        }

        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Изменение пароля", Toast.LENGTH_SHORT).show()
            // Здесь можно открыть экран изменения пароля
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}