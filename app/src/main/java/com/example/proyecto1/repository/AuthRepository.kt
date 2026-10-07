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

    // Firebase guarda la sesión, así que si el usuario no cerró sesión esto no es null
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun login(email: String, password: String, onResult: (Resource<FirebaseUser>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                onResult(Resource.Success(result.user!!))
            }
            .addOnFailureListener { exception ->
                onResult(Resource.Error(exception))
            }
    }

    fun register(name: String, email: String, password: String, onResult: (Resource<FirebaseUser>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user!!

                // Además de crear la cuenta, se guarda el usuario en Firestore
                // para poder mostrarlo después en la lista de usuarios
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

    fun logout ()
    {
        auth.signOut()


    }




}
