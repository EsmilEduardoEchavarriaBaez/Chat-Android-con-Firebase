package com.example.proyecto1.service

import com.example.proyecto1.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val userRepository = UserRepository()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Aquí irán las notificaciones (lo harás después)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Guardar el token en la base de datos
        userRepository.updateFcmToken(token) { result ->
            // El token se guardó
        }
    }
}