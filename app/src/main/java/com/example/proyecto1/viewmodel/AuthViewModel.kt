package com.example.proyecto1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto1.model.Resource
import com.example.proyecto1.repository.AuthRepository
import com.example.proyecto1.util.Validators
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    // La Activity observa este estado: Loading, Success o Error
    private val _authState = MutableLiveData<Resource<FirebaseUser>>()
    val authState: LiveData<Resource<FirebaseUser>> = _authState

    // Indica si ya hay una sesión iniciada
    fun isUserLoggedIn(): Boolean {
        return repository.getCurrentUser() != null
    }

    // Valida los datos y pide al repositorio iniciar sesión
    fun login(email: String, password: String) {
        val error = validateEmailAndPassword(email, password)
        if (error != null) {
            _authState.value = Resource.Error(Exception(error))
            return
        }

        _authState.value = Resource.Loading
        repository.login(email.trim(), password) { result ->
            _authState.value = toFriendlyResult(result)
        }
    }

    // Valida los datos y pide al repositorio crear la cuenta
    fun register(name: String, email: String, password: String) {
        val error = validateRegister(name, email, password)
        if (error != null) {
            _authState.value = Resource.Error(Exception(error))
            return
        }

        _authState.value = Resource.Loading
        repository.register(name.trim(), email.trim(), password) { result ->
            _authState.value = toFriendlyResult(result)
        }
    }

    // Revisa que el correo y la contraseña no estén vacíos y que el correo sea válido
    private fun validateEmailAndPassword(email: String, password: String): String? {
        if (Validators.isEmptyField(email) || Validators.isEmptyField(password)) {
            return "Completa todos los campos"
        }
        if (!Validators.isValidEmail(email.trim())) {
            return "El correo no tiene un formato válido"
        }
        return null
    }

    // Valida todos los campos del registro
    private fun validateRegister(name: String, email: String, password: String): String? {
        if (Validators.isEmptyField(name)) {
            return "Completa todos los campos"
        }
        val error = validateEmailAndPassword(email, password)
        if (error != null) {
            return error
        }
        if (!Validators.isValidPassword(password)) {
            return "La contraseña debe tener al menos 6 caracteres"
        }
        return null
    }

    // Cambia el error de Firebase por un mensaje en español
    private fun toFriendlyResult(result: Resource<FirebaseUser>): Resource<FirebaseUser> {
        if (result !is Resource.Error) {
            return result
        }
        val message = when (result.exception) {
            is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil"
            is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
            is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo"
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo"
            is FirebaseNetworkException -> "No hay conexión a internet"
            else -> "Ocurrió un error: ${result.exception.message}"
        }
        return Resource.Error(Exception(message))
    }

    // Cierra la sesión del usuario
    fun logout() {
        repository.logout()
    }
}
