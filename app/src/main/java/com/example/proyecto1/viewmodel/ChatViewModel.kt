package com.example.proyecto1.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.proyecto1.model.Message
import com.example.proyecto1.model.Resource
import com.example.proyecto1.repository.ChatRepository
import com.example.proyecto1.repository.StorageRepository
import com.example.proyecto1.repository.UserRepository
import com.example.proyecto1.util.Constants
import com.example.proyecto1.util.ImageCompressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Es AndroidViewModel porque necesita el contexto de la app para comprimir las imágenes
class ChatViewModel(application: Application) : AndroidViewModel(application) {

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

    // El uid se lee una sola vez y se reutiliza en cada mensaje
    private val currentUserId = userRepository.getCurrentUser() ?: ""

    private var chatId = ""
    private var otherUserId = ""
    private var currentUserName = ""

    // Cuántos mensajes se están mostrando; crece de a una página al subir en el chat
    private var messagesLimit = Constants.MESSAGES_PAGE_SIZE
    private var isLoadingOlder = false
    private var hasOlderMessages = true

    // Devuelve el uid del usuario actual
    fun getCurrentUserId(): String {
        return currentUserId
    }

    // Inicia el chat con mi nombre (que llega de la lista de usuarios) y empieza a escuchar los mensajes
    fun startChat(otherUserId: String, currentUserName: String) {
        // Si se gira la pantalla el ViewModel se mantiene, así que no se vuelve a iniciar
        if (chatId.isNotEmpty()) {
            return
        }

        this.otherUserId = otherUserId
        this.currentUserName = currentUserName
        chatId = chatRepository.getChatId(currentUserId, otherUserId)

        _messages.value = Resource.Loading
        listenMessages()
    }

    // Escucha los mensajes del chat hasta el límite actual
    private fun listenMessages() {
        chatRepository.listenMessages(chatId, messagesLimit) { result ->
            isLoadingOlder = false
            if (result is Resource.Success) {
                // Si llegaron menos mensajes que el límite es porque ya no hay más antiguos
                hasOlderMessages = result.data.size >= messagesLimit
            }
            _messages.value = result
        }
    }

    // Pide otra página de mensajes anteriores cuando el usuario llega arriba del chat
    fun loadOlderMessages() {
        if (isLoadingOlder || !hasOlderMessages) {
            return
        }
        isLoadingOlder = true
        messagesLimit += Constants.MESSAGES_PAGE_SIZE
        listenMessages()
    }

    // Envía un mensaje de texto si no está vacío
    fun sendMessage(text: String) {
        val cleanText = text.trim()

        // No se envían mensajes vacíos
        if (cleanText.isEmpty()) {
            return
        }

        val message = Message(
            senderId = currentUserId,
            text = cleanText,
            type = Constants.MESSAGE_TYPE_TEXT
        )
        saveMessage(message)
    }

    // Comprime la imagen, la sube y después envía el mensaje con su URL
    fun sendImage(imageUri: Uri) {
        _isUploading.value = true
        viewModelScope.launch {
            // La compresión se hace en segundo plano para no congelar la pantalla
            val imageData = withContext(Dispatchers.IO) {
                ImageCompressor.compressImage(getApplication(), imageUri)
            }
            if (imageData == null) {
                _isUploading.value = false
                _error.value = "No se pudo leer la imagen"
                return@launch
            }
            uploadImage(imageData)
        }
    }

    // Sube la imagen a Storage y si sale bien guarda el mensaje con su URL
    private fun uploadImage(imageData: ByteArray) {
        storageRepository.uploadImage(chatId, imageData) { result ->
            _isUploading.value = false
            when (result) {
                is Resource.Success -> {
                    val message = Message(
                        senderId = currentUserId,
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

    // Agrega mi nombre al mensaje, lo guarda en la base de datos y avisa si hay un error
    private fun saveMessage(message: Message) {
        chatRepository.sendMessage(chatId, otherUserId, message.copy(senderName = currentUserName)) { result ->
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
