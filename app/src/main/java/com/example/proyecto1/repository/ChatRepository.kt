package com.example.proyecto1.repository

import com.example.proyecto1.model.Message
import com.example.proyecto1.model.Resource
import com.example.proyecto1.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ChatRepository {

    private val db = FirebaseDatabase.getInstance().reference

    // Se guardan para poder quitar el listener cuando se cierra el chat
    private var messagesQuery: Query? = null
    private var messagesListener: ValueEventListener? = null

    // El chat entre dos usuarios siempre tiene el mismo id, sin importar quién lo abra
    fun getChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_$uid2" else "${uid2}_$uid1"
    }

    fun sendMessage(chatId: String, message: Message, onResult: (Resource<Unit>) -> Unit) {
        // push() crea un id único para el mensaje
        val messageRef = db.child(Constants.CHATS_COLLECTION)
            .child(chatId)
            .child(Constants.MESSAGES_COLLECTION)
            .push()

        val messageWithId = message.copy(id = messageRef.key ?: "")
        messageRef.setValue(messageWithId)
            .addOnSuccessListener {
                onResult(Resource.Success(Unit))
            }
            .addOnFailureListener { exception ->
                onResult(Resource.Error(exception))
            }
    }

    // Escucha los mensajes en tiempo real: se llama cada vez que alguien envía uno
    fun listenMessages(chatId: String, onResult: (Resource<List<Message>>) -> Unit) {
        val query = db.child(Constants.CHATS_COLLECTION)
            .child(chatId)
            .child(Constants.MESSAGES_COLLECTION)
            .orderByChild("timestamp")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (child in snapshot.children) {
                    val message = child.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                onResult(Resource.Success(messages))
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(Resource.Error(error.toException()))
            }
        }

        query.addValueEventListener(listener)
        messagesQuery = query
        messagesListener = listener
    }

    fun stopListening() {
        val listener = messagesListener ?: return
        messagesQuery?.removeEventListener(listener)
    }
}
