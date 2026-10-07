package com.example.proyecto1.repository

import android.util.Log
import com.example.proyecto1.model.Message
import com.example.proyecto1.util.Constants
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NotificationRepository {

    private val db = FirebaseDatabase.getInstance().reference

    // Se guardan para poder quitar el listener cuando se cierra la pantalla
    private var inboxRef: DatabaseReference? = null
    private var inboxListener: ChildEventListener? = null

    // Escucha la bandeja del usuario y avisa por cada mensaje nuevo que le llega
    fun listenNewMessages(uid: String, onNewMessage: (Message) -> Unit) {
        val ref = db.child(Constants.NOTIFICATIONS_COLLECTION).child(uid)
        Log.d(Constants.NOTIFICATIONS_LOG_TAG, "Escuchando la bandeja de $uid")

        val listener = object : ChildEventListener {
            // Se ejecuta una vez por cada aviso nuevo en la bandeja
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(Constants.NOTIFICATIONS_LOG_TAG, "Llegó un aviso: ${snapshot.key}")
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    onNewMessage(message)
                }
                // Se borra para no volver a notificar el mismo mensaje
                snapshot.ref.removeValue()
            }

            // No se usa porque los avisos no se editan
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            // No se usa porque el aviso lo borra esta misma app
            override fun onChildRemoved(snapshot: DataSnapshot) {}

            // No se usa porque los avisos no cambian de orden
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            // Se ejecuta si Firebase no deja leer la bandeja, por ejemplo por las reglas
            override fun onCancelled(error: DatabaseError) {
                Log.e(Constants.NOTIFICATIONS_LOG_TAG, "No se pudo leer la bandeja: ${error.message}")
            }
        }

        ref.addChildEventListener(listener)
        inboxRef = ref
        inboxListener = listener
    }

    // Deja de escuchar la bandeja del usuario
    fun stopListening() {
        val listener = inboxListener ?: return
        inboxRef?.removeEventListener(listener)
    }
}
