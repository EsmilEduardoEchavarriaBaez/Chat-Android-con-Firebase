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
    private var currentUserName = ""

    fun getCurrentUserId(): String {
        return userRepository.getCurrentUser() ?: ""
    }

    fun startChat(otherUserId: String) {
        // Si se gira la pantalla el ViewModel se mantiene, así que no se vuelve a iniciar
        if (chatId.isNotEmpty()) {
            return
        }

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

    fun sendMessage(text: String) {
        val cleanText = text.trim()

        // No se envían mensajes vacíos
        if (cleanText.isEmpty()) {
            return
        }

        val message = Message(
            senderId = getCurrentUserId(),
            senderName = currentUserName,
            text = cleanText,
            type = Constants.MESSAGE_TYPE_TEXT
        )
        saveMessage(message)
    }

    // Primero se sube la imagen a Storage y después se guarda el mensaje con su URL
    fun sendImage(imageUri: Uri) {
        _isUploading.value = true
        storageRepository.uploadImage(chatId, imageUri) { result ->
            _isUploading.value = false
            when (result) {
                is Resource.Success -> {
                    val message = Message(
                        senderId = getCurrentUserId(),
                        senderName = currentUserName,
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

    private fun saveMessage(message: Message) {
        chatRepository.sendMessage(chatId, message) { result ->
            if (result is Resource.Error) {
                _error.value = "No se pudo enviar el mensaje: ${result.exception.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.stopListening()
    }
}
