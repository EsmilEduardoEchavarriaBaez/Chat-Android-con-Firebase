package com.example.proyecto1.service

import com.example.proyecto1.repository.UserRepository
import com.example.proyecto1.util.NotificationUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val userRepository = UserRepository()

    // Se llama cuando llega una notificación con la app abierta.
    // Con la app cerrada o en segundo plano, Android la muestra solo.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Se acepta tanto el formato "notification" (consola de Firebase) como "data"
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Nuevo mensaje"
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: ""

        NotificationUtils.showNotification(this, title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Guardar el token en la base de datos
        userRepository.updateFcmToken(token) { result ->
            // El token se guardó
        }
    }
}
