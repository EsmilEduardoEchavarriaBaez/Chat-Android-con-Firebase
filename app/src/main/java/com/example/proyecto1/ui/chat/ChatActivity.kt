package com.example.proyecto1.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ActivityChatBinding
import com.example.proyecto1.model.Message
import com.example.proyecto1.model.Resource
import com.example.proyecto1.util.ActiveChat
import com.example.proyecto1.util.Constants
import com.example.proyecto1.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: MessageAdapter
    private val viewModel: ChatViewModel by viewModels()
    private var otherUserId = ""

    // Abre la galería y devuelve la imagen que el usuario eligió (o null si canceló)
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                viewModel.sendImage(uri)
            }
        }

    // Prepara la pantalla del chat, sus botones y observa los mensajes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Carga el diseño XML de la pantalla del chat
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Datos del otro usuario que llegan desde la lista de usuarios
        otherUserId = intent.getStringExtra(Constants.EXTRA_USER_ID) ?: ""
        val otherUserName = intent.getStringExtra(Constants.EXTRA_USER_NAME) ?: ""
        // Sin el uid del otro usuario no hay chat que abrir, así que se cierra la pantalla
        if (otherUserId.isEmpty()) {
            finish()
            return
        }
        // El nombre del contacto va como título del chat
        binding.tvChatTitle.text = otherUserName
        // En el círculo de arriba se pone la primera letra del nombre
        binding.tvAvatar.text = otherUserName.take(1).uppercase().ifEmpty { "?" }
        // Texto que se muestra en el centro cuando todavía no hay mensajes
        binding.tvEmpty.text = getString(R.string.chat_empty, otherUserName.ifEmpty { "este usuario" })

        // La flecha cierra el chat y vuelve a la lista de usuarios
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Prepara la lista donde se muestran los mensajes
        setupRecyclerView()

        // El botón de enviar se activa o se apaga según si hay texto escrito
        updateSendButton()
        binding.etMessage.doAfterTextChanged {
            updateSendButton()
        }

        // Al enviar: el botón rebota, el texto va al ViewModel y la caja se limpia
        binding.btnSend.setOnClickListener {
            animateSendButton()
            viewModel.sendMessage(binding.etMessage.text.toString())
            binding.etMessage.setText("")
        }

        // El botón de imagen abre la galería para elegir una foto
        binding.btnImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Mientras se sube la imagen se muestra una ruedita y no se puede enviar otra
        viewModel.isUploading.observe(this) { isUploading ->
            binding.pbUpload.visibility = if (isUploading) View.VISIBLE else View.GONE
            binding.btnImage.isEnabled = !isUploading
        }

        // Según el estado de los mensajes: muestra la carga, la conversación o el error
        viewModel.messages.observe(this) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showMessages(state.data)
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Si algo falla al enviar o subir una imagen, se muestra un aviso corto
        viewModel.error.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        // Mi nombre llega desde la lista de usuarios para ponerlo en cada mensaje que envíe
        val currentUserName = intent.getStringExtra(Constants.EXTRA_CURRENT_USER_NAME) ?: ""
        viewModel.startChat(otherUserId, currentUserName)
    }

    // Marca este chat como abierto para no recibir notificaciones de él
    override fun onResume() {
        super.onResume()
        ActiveChat.userId = otherUserId
    }

    // Marca que ya no se está viendo este chat
    override fun onPause() {
        super.onPause()
        ActiveChat.userId = null
    }

    // Configura la lista de mensajes para que empiece desde abajo
    private fun setupRecyclerView() {
        // El adaptador recibe mi uid para saber qué mensajes van a la derecha
        adapter = MessageAdapter(viewModel.getCurrentUserId())
        val layoutManager = LinearLayoutManager(this)
        // Igual que en WhatsApp, la lista empieza desde abajo
        layoutManager.stackFromEnd = true
        binding.rvMessages.layoutManager = layoutManager
        // La lista no cambia de tamaño con su contenido, así se evitan cálculos de más
        binding.rvMessages.setHasFixedSize(true)
        binding.rvMessages.adapter = adapter

        // Cuando el usuario sube hasta el primer mensaje se piden los anteriores
        binding.rvMessages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            // Se ejecuta cada vez que se desplaza la lista
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Si se está subiendo y ya no se puede subir más, se piden los mensajes anteriores
                if (dy < 0 && !recyclerView.canScrollVertically(-1)) {
                    viewModel.loadOlderMessages()
                }
            }
        })
    }

    // Muestra los mensajes y baja hasta el final solo si llegó uno nuevo
    private fun showMessages(messages: List<Message>) {
        // Si todavía no hay mensajes se muestra un aviso en el centro
        binding.tvEmpty.visibility = if (messages.isEmpty()) View.VISIBLE else View.GONE

        // Se guarda el último mensaje que había para saber después si llegó uno nuevo
        val lastIdBefore = adapter.currentList.lastOrNull()?.id
        // submitList calcula las diferencias y solo redibuja los mensajes que cambiaron
        adapter.submitList(messages) {
            // Si solo se agregaron mensajes viejos arriba, la lista se queda donde estaba
            val lastIdNow = messages.lastOrNull()?.id
            if (messages.isNotEmpty() && lastIdNow != lastIdBefore) {
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
        }
    }

    // Activa el botón de enviar solo cuando hay texto y lo deja medio transparente si no
    private fun updateSendButton() {
        val hasText = binding.etMessage.text.toString().trim().isNotEmpty()
        binding.btnSend.isEnabled = hasText
        binding.btnSend.alpha = if (hasText) 1f else 0.4f
    }

    // Hace un pequeño efecto de rebote en el botón al enviar
    private fun animateSendButton() {
        binding.btnSend.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(90)
            .withEndAction {
                binding.btnSend.animate().scaleX(1f).scaleY(1f).setDuration(90).start()
            }
            .start()
    }
}
