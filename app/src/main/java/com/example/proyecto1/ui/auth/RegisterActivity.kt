package com.example.proyecto1.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto1.databinding.ActivityRegisterBinding
import com.example.proyecto1.model.Resource
import com.example.proyecto1.ui.users.UsersActivity
import com.example.proyecto1.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    // Prepara la pantalla de registro y sus botones
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.register(name, email, password)
        }

        // El login sigue abierto debajo, así que solo se cierra esta pantalla
        binding.tvGoToLogin.setOnClickListener {
            finish()
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

    // Muestra u oculta la carga y bloquea el botón mientras espera
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        if (isLoading) {
            binding.tvError.visibility = View.GONE
        }
    }

    // Abre la lista de usuarios y borra las pantallas anteriores
    private fun goToUsers() {
        // Se limpia la pila para que al dar "atrás" no vuelva al registro ni al login
        val intent = Intent(this, UsersActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
