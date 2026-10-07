package com.example.proyecto1.service

import com.example.proyecto1.repository.UserRepository
import com.example.proyecto1.util.NotificationUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val userRepository = UserRepository()

    // Se ejecuta al llegar una notificación y la muestra en pantalla
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

    // Se ejecuta cuando cambia el token y lo guarda en la base de datos
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Guardar el token en la base de datos
        userRepository.updateFcmToken(token) { result ->
            // El token se guardó
        }
    }
}
