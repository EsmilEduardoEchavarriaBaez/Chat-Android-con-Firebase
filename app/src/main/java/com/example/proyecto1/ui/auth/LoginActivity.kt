package com.example.proyecto1.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto1.databinding.ActivityLoginBinding
import com.example.proyecto1.model.Resource
import com.example.proyecto1.ui.users.UsersActivity
import com.example.proyecto1.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si el usuario no cerró sesión, entra directo sin pasar por el login
        if (viewModel.isUserLoggedIn()) {
            goToUsers()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.authState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    goToUsers()
                }
                is Resource.Error -> {
                    showLoading(false)
                    binding.tvError.text = state.exception.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        if (isLoading) {
            binding.tvError.visibility = View.GONE
        }
    }

    private fun goToUsers() {
        startActivity(Intent(this, UsersActivity::class.java))
        finish()
    }
}
