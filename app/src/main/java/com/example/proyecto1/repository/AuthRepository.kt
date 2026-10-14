package com.example.proyecto1.repository

import com.example.proyecto1.model.Resource
import com.example.proyecto1.model.User
import com.example.proyecto1.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // Devuelve el usuario con sesión iniciada o null si no hay sesión
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Inicia sesión en Firebase con correo y contraseña
    fun login(email: String, password: String, onResult: (Resource<FirebaseUser>) -> Unit) {
        // Si Firebase acepta el correo y la contraseña devuelve el usuario; si no, el error
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                onResult(Resource.Success(result.user!!))
            }
            .addOnFailureListener { exception ->
                onResult(Resource.Error(exception))
            }
    }

    // Crea la cuenta y guarda los datos del usuario en la base de datos
    fun register(name: String, email: String, password: String, onResult: (Resource<FirebaseUser>) -> Unit) {
        // Primero se crea la cuenta en Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user!!

                // Se guarda el usuario en users/{uid} para mostrarlo después en la lista de usuarios
                val user = User(uid = firebaseUser.uid, name = name, email = email)
                db.child(Constants.USERS_COLLECTION)
                    .child(firebaseUser.uid)
                    .setValue(user)
                    .addOnSuccessListener {
                        onResult(Resource.Success(firebaseUser))
                    }
                    .addOnFailureListener { exception ->
                        onResult(Resource.Error(exception))
                    }
            }
            .addOnFailureListener { exception ->
                onResult(Resource.Error(exception))
            }
    }
}
