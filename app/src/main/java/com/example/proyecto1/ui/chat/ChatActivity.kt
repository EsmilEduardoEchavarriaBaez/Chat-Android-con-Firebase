package com.example.proyecto1.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Datos del otro usuario que llegan desde la lista de usuarios
        otherUserId = intent.getStringExtra(Constants.EXTRA_USER_ID) ?: ""
        val otherUserName = intent.getStringExtra(Constants.EXTRA_USER_NAME) ?: ""
        if (otherUserId.isEmpty()) {
            finish()
            return
        }
        binding.tvChatTitle.text = otherUserName

        // La flecha cierra el chat y vuelve a la lista de usuarios
        binding.btnBack.setOnClickListener {
            finish()
        }

        setupRecyclerView()

        binding.btnSend.setOnClickListener {
            viewModel.sendMessage(binding.etMessage.text.toString())
            binding.etMessage.setText("")
        }

        binding.btnImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Mientras se sube la imagen se muestra una ruedita y no se puede enviar otra
        viewModel.isUploading.observe(this) { isUploading ->
            binding.pbUpload.visibility = if (isUploading) View.VISIBLE else View.GONE
            binding.btnImage.isEnabled = !isUploading
        }

        viewModel.messages.observe(this) { state ->
            when (state) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
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

        viewModel.error.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        viewModel.startChat(otherUserId)
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
                if (dy < 0 && !recyclerView.canScrollVertically(-1)) {
                    viewModel.loadOlderMessages()
                }
            }
        })
    }

    // Muestra los mensajes y baja hasta el final solo si llegó uno nuevo
    private fun showMessages(messages: List<Message>) {
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
}
