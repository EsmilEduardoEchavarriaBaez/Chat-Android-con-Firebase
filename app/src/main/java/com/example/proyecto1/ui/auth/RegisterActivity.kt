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
        // Carga el diseño XML de la pantalla de registro
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Al tocar "Crear cuenta" se leen los tres campos y se le pasan al ViewModel
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.register(name, email, password)
        }

        // "¿Ya tienes cuenta?" cierra esta pantalla y vuelve al login, que sigue abierto debajo
        binding.tvGoToLogin.setOnClickListener {
            finish()
        }

        // Según el estado del registro: muestra la carga, entra a la lista o muestra el error
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
        // Al empezar una carga nueva se esconde el error anterior
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
