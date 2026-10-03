package com.example.projecto1.util

object Validators {

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isEmptyField(field: String): Boolean {
        return field.trim().isEmpty()
    }
}