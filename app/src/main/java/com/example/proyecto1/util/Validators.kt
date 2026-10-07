package com.example.proyecto1.util

object Validators {

    // Revisa que el correo tenga un formato válido
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Revisa que la contraseña tenga al menos 6 caracteres
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    // Revisa si un campo está vacío
    fun isEmptyField(field: String): Boolean {
        return field.trim().isEmpty()
    }
}