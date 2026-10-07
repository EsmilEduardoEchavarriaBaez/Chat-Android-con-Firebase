package com.example.proyecto1.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.proyecto1.model.Resource
import com.example.proyecto1.model.User
import com.example.proyecto1.repository.NotificationRepository
import com.example.proyecto1.repository.UserRepository
import com.example.proyecto1.util.ActiveChat
import com.example.proyecto1.util.Constants
import com.example.proyecto1.util.NotificationUtils


// Es AndroidViewModel para poder mostrar notificaciones con el contexto de la app
class UsersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository()
    private val notificationRepository = NotificationRepository()

    private val _usersList = MutableLiveData<Resource<List<User>>>()
    val usersList: LiveData<Resource<List<User>>> = _usersList

    private var isListeningNotifications = false

    // Pide la lista de usuarios al repositorio
    fun loadUsers() {
        _usersList.value = Resource.Loading
        repository.getAllUsersExceptCurrent { result ->
            _usersList.value = result
        }
    }

    // Guarda el token de notificaciones del usuario
    fun saveFcmToken() {
        repository.saveCurrentFcmToken()
    }

    // Escucha los mensajes nuevos y muestra una notificación si ese chat no está abierto
    fun startNotifications() {
        // Si se gira la pantalla el ViewModel se mantiene, así que no se vuelve a escuchar
        if (isListeningNotifications) {
            return
        }
        val uid = repository.getCurrentUser() ?: return
        isListeningNotifications = true

        notificationRepository.listenNewMessages(uid) { message ->
            // Si ya se está chateando con esa persona no hace falta avisar
            if (message.senderId != ActiveChat.userId) {
                val sender = message.senderName.ifEmpty { "un usuario" }
                val body = if (message.type == Constants.MESSAGE_TYPE_IMAGE) "Te envió una imagen" else message.text
                NotificationUtils.showNotification(getApplication(), "Nuevo mensaje de $sender", body)
            }
        }
    }

    // Cierra la sesión del usuario
    fun logout() {
        repository.logout()
    }

    // Se ejecuta al cerrar la pantalla de usuarios y deja de escuchar la bandeja
    override fun onCleared() {
        super.onCleared()
        notificationRepository.stopListening()
    }

}
