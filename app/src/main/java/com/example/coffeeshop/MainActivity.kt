package com.example.coffeeshop

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.coffeeshop.databinding.ActivityMainBinding
import com.example.coffeeshop.managers.NotificationManager
import com.example.coffeeshop.models.CartManager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance() // Изменено здесь
        notificationManager = NotificationManager(this)

        CartManager.init(this)

        setupUI()
        checkAuth()
        setupNotifications()
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    private fun setupUI() {
        val user = auth.currentUser
        user?.let {
            binding.tvWelcome.text = "Добро пожаловать, ${it.displayName ?: it.email?.split("@")?.get(0) ?: "Гость"}!"
        }

        binding.cardMenu.setOnClickListener { navigateToMenu() }
        binding.cardCart.setOnClickListener { navigateToCart() }
        binding.cardOrders.setOnClickListener { navigateToOrders() }
        binding.cardProfile.setOnClickListener { navigateToProfile() }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> { logout(); true }
                R.id.action_settings -> {
                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        updateCartBadge()
    }

    private fun updateCartBadge() {
        val totalItems = CartManager.getTotalItems()
        // Удалите или закомментируйте строки с несуществующими view
        // binding.cartBadge.visibility = View.VISIBLE
        // binding.tvCartBadge.visibility = View.VISIBLE

        // Вместо этого можно добавить бейдж в layout или использовать другую логику
        // Например, показать Toast или обновить TextView в тулбаре
    }

    private fun checkAuth() {
        if (auth.currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun setupNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            } else {
                notificationManager.subscribeToTopics()
            }
        } else {
            notificationManager.subscribeToTopics()
        }

        notificationManager.getFCMToken { token ->
            token?.let {
                val prefs = getSharedPreferences("coffee_shop_prefs", MODE_PRIVATE)
                prefs.edit().putString("fcm_token", token).apply()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                notificationManager.subscribeToTopics()
                Toast.makeText(this, "Уведомления включены!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Уведомления отключены", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun navigateToCart() {
        startActivity(Intent(this, CartActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun navigateToOrders() {
        val intent = Intent(this, OrdersActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show()

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        } else {
            super.onBackPressed()
        }
    }

    private var doubleBackToExitPressedOnce = false
}