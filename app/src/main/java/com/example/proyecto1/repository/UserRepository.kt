package com.example.proyecto1.repository



import com.example.proyecto1.model.Resource
import com.example.proyecto1.model.User
import com.example.proyecto1.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging


class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // Obtener el usuario actual
    fun getCurrentUser(): String? {
        return auth.currentUser?.uid
    }

    // Obtener todos los usuarios EXCEPTO el actual
    fun getAllUsersExceptCurrent(onResult: (Resource<List<User>>) -> Unit)
    {
        val currentUid = auth.currentUser?.uid ?: run {
            onResult(Resource.Error(Exception("Usuario no indetificado")))
            return
        }


        db.child(Constants.USERS_COLLECTION).get()
            .addOnSuccessListener {
                snapshot ->
                val users = mutableListOf<User>()
                for (child in snapshot.children)
                {
                    val user = child.getValue(User::class.java)
                    if (user != null && user.uid != currentUid)
                    {
                        users.add(user)
                    }
                }
                onResult(Resource.Success(users))
            }
            .addOnFailureListener { exception ->
                onResult(Resource.Error(exception))
            }


    }

    fun updateFcmToken(fcmToken: String, onResult: (Resource<Unit>) -> Unit)
    {
        val uid = auth.currentUser?.uid ?: run{
            onResult(Resource.Error(Exception("Usuario no indetificado")))
            return
        }

        db.child(Constants.USERS_COLLECTION)
            .child(uid)
            .child("fcmToken")
            .setValue(fcmToken)
            .addOnSuccessListener {
                onResult(Resource.Success(Unit))
            }
            .addOnFailureListener { exception ->
                onResult(Resource.Error(exception))
            }
    }

    // onNewToken solo se llama cuando el token cambia, así que también
    // se pide y se guarda cada vez que el usuario entra a la app
    fun saveCurrentFcmToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                updateFcmToken(token) { }
            }
    }

    // Obtener un usuario por UID
    fun getUserById(uid: String, onResult: (Resource<User>) -> Unit)
    {
        db.child(Constants.USERS_COLLECTION)
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                if (user != null)
                {
                    onResult(Resource.Success(user))
                } else {
                    onResult(Resource.Error(Exception("Usuario no verificado")))
                }
            }
            .addOnFailureListener { exception ->
                onResult(Resource.Error(exception))
            }
    }

    fun logout() {
        auth.signOut()
    }



}