package com.example.proyecto1.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto1.model.Message
import com.example.proyecto1.model.Resource
import com.example.proyecto1.repository.ChatRepository
import com.example.proyecto1.repository.StorageRepository
import com.example.proyecto1.repository.UserRepository
import com.example.proyecto1.util.Constants

class ChatViewModel : ViewModel() {

    private val chatRepository = ChatRepository()
    private val userRepository = UserRepository()
    private val storageRepository = StorageRepository()

    // true mientras se está subiendo una imagen
    private val _isUploading = MutableLiveData(false)
    val isUploading: LiveData<Boolean> = _isUploading

    private val _messages = MutableLiveData<Resource<List<Message>>>()
    val messages: LiveData<Resource<List<Message>>> = _messages

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var chatId = ""
    private var otherUserId = ""
    private var currentUserName = ""

    // Devuelve el uid del usuario actual
    fun getCurrentUserId(): String {
        return userRepository.getCurrentUser() ?: ""
    }

    // Inicia el chat, busca el nombre del usuario y empieza a escuchar los mensajes
    fun startChat(otherUserId: String) {
        // Si se gira la pantalla el ViewModel se mantiene, así que no se vuelve a iniciar
        if (chatId.isNotEmpty()) {
            return
        }

        this.otherUserId = otherUserId
        val currentUserId = getCurrentUserId()
        chatId = chatRepository.getChatId(currentUserId, otherUserId)

        // El nombre se busca una vez para ponerlo en cada mensaje que se envíe
        userRepository.getUserById(currentUserId) { result ->
            if (result is Resource.Success) {
                currentUserName = result.data.name
            }
        }

        _messages.value = Resource.Loading
        chatRepository.listenMessages(chatId) { result ->
            _messages.value = result
        }
    }

    // Envía un mensaje de texto si no está vacío
    fun sendMessage(text: String) {
        val cleanText = text.trim()

        // No se envían mensajes vacíos
        if (cleanText.isEmpty()) {
            return
        }

        val message = Message(
            senderId = getCurrentUserId(),
            text = cleanText,
            type = Constants.MESSAGE_TYPE_TEXT
        )
        saveMessage(message)
    }

    // Sube la imagen y después envía el mensaje con su URL
    fun sendImage(imageUri: Uri) {
        _isUploading.value = true
        storageRepository.uploadImage(chatId, imageUri) { result ->
            _isUploading.value = false
            when (result) {
                is Resource.Success -> {
                    val message = Message(
                        senderId = getCurrentUserId(),
                        imageUrl = result.data,
                        type = Constants.MESSAGE_TYPE_IMAGE
                    )
                    saveMessage(message)
                }
                is Resource.Error -> {
                    _error.value = "No se pudo subir la imagen: ${result.exception.message}"
                }
                is Resource.Loading -> {}
            }
        }
    }

    // Agrega el nombre del usuario al mensaje y lo envía
    private fun saveMessage(message: Message) {
        // Si el nombre todavía no había llegado, se busca antes de enviar
        if (currentUserName.isEmpty()) {
            userRepository.getUserById(getCurrentUserId()) { result ->
                if (result is Resource.Success) {
                    currentUserName = result.data.name
                }
                sendToChat(message.copy(senderName = currentUserName))
            }
        } else {
            sendToChat(message.copy(senderName = currentUserName))
        }
    }

    // Guarda el mensaje en la base de datos y avisa si hay un error
    private fun sendToChat(message: Message) {
        chatRepository.sendMessage(chatId, otherUserId, message) { result ->
            if (result is Resource.Error) {
                _error.value = "No se pudo enviar el mensaje: ${result.exception.message}"
            }
        }
    }

    // Se ejecuta al cerrar el chat y deja de escuchar los mensajes
    override fun onCleared() {
        super.onCleared()
        chatRepository.stopListening()
    }
}
