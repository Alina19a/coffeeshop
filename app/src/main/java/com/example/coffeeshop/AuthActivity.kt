package com.example.coffeeshop

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AuthActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnRegister: MaterialButton
    private lateinit var progressBar: ProgressBar

    private lateinit var sharedPreferences: SharedPreferences
    private companion object {
        const val PREF_NAME = "CoffeeShopPrefs"
        const val KEY_EMAIL = "user_email"
        const val KEY_PASSWORD = "user_password"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        // Check if user is already logged in
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            startMainActivity()
            return
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener { login() }
        btnRegister.setOnClickListener { register() }
    }

    private fun login() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (!validateInputs(email, password)) {
            return
        }

        showLoading(true)

        // Simulate network delay
        Handler(Looper.getMainLooper()).postDelayed({
            val savedEmail = sharedPreferences.getString(KEY_EMAIL, "")
            val savedPassword = sharedPreferences.getString(KEY_PASSWORD, "")

            if (email == savedEmail && password == savedPassword) {
                // Successful login
                sharedPreferences.edit()
                    .putBoolean(KEY_IS_LOGGED_IN, true)
                    .apply()

                startMainActivity()
            } else {
                showLoading(false)
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }, 1000)
    }

    private fun register() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (!validateInputs(email, password)) {
            return
        }

        if (password.length < 6) {
            passwordLayout.error = "Password must be at least 6 characters"
            return
        }

        showLoading(true)

        // Simulate network delay for registration
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user already exists
            val savedEmail = sharedPreferences.getString(KEY_EMAIL, "")
            if (email == savedEmail) {
                showLoading(false)
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                return@postDelayed
            }

            // Save user credentials
            sharedPreferences.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply()

            startMainActivity()
        }, 1000)
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        // Reset errors
        emailLayout.error = null
        passwordLayout.error = null

        if (email.isEmpty()) {
            emailLayout.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = "Please enter a valid email"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordLayout.error = "Password is required"
            isValid = false
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false
            btnRegister.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            btnLogin.isEnabled = true
            btnRegister.isEnabled = true
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}