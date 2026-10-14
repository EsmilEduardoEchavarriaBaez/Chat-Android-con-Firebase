package com.example.proyecto1.util

object Constants {

    const val USERS_COLLECTION = "users"
    const val CHATS_COLLECTION = "chats"
    const val MESSAGES_COLLECTION = "messages"
    const val NOTIFICATIONS_COLLECTION = "notifications"

    const val EXTRA_USER_ID = "userId"
    const val EXTRA_USER_NAME = "userName"
    const val EXTRA_CURRENT_USER_NAME = "currentUserName"

    const val CHAT_IMAGES_FOLDER = "chat_images"
    const val MESSAGE_TYPE_TEXT = "TEXT"
    const val MESSAGE_TYPE_IMAGE = "IMAGE"

    // Cantidad de mensajes que se cargan cada vez (los más recientes primero)
    const val MESSAGES_PAGE_SIZE = 50

    // Etiqueta para buscar en Logcat todo lo relacionado con las notificaciones
    const val NOTIFICATIONS_LOG_TAG = "Notificaciones"
}