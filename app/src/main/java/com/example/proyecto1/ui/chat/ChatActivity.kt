package com.example.proyecto1.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto1.databinding.ActivityChatBinding
import com.example.proyecto1.model.Resource
import com.example.proyecto1.util.Constants
import com.example.proyecto1.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: MessageAdapter
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Datos del otro usuario que llegan desde la lista de usuarios
        val otherUserId = intent.getStringExtra(Constants.EXTRA_USER_ID) ?: ""
        val otherUserName = intent.getStringExtra(Constants.EXTRA_USER_NAME) ?: ""
        if (otherUserId.isEmpty()) {
            finish()
            return
        }
        binding.tvChatTitle.text = otherUserName

        setupRecyclerView()

        binding.btnSend.setOnClickListener {
            viewModel.sendMessage(binding.etMessage.text.toString())
            binding.etMessage.setText("")
        }

        viewModel.messages.observe(this) { state ->
            when (state) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.setMessages(state.data)
                    // Baja hasta el último mensaje
                    if (state.data.isNotEmpty()) {
                        binding.rvMessages.scrollToPosition(state.data.size - 1)
                    }
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

    private fun setupRecyclerView() {
        adapter = MessageAdapter(viewModel.getCurrentUserId())
        val layoutManager = LinearLayoutManager(this)
        // Igual que en WhatsApp, la lista empieza desde abajo
        layoutManager.stackFromEnd = true
        binding.rvMessages.layoutManager = layoutManager
        binding.rvMessages.adapter = adapter
    }
}
