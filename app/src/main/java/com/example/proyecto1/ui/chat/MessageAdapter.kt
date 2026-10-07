package com.example.proyecto1.ui.chat

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ItemMessageBinding
import com.example.proyecto1.model.Message
import com.example.proyecto1.util.Constants
import com.example.proyecto1.util.DateUtils

class MessageAdapter(
    private val currentUserId: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages: List<Message> = emptyList()

    // Reemplaza la lista de mensajes y refresca la pantalla
    @SuppressLint("NotifyDataSetChanged")
    fun setMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        // Llena una burbuja con el nombre, el texto o la imagen y la hora
        fun bind(message: Message) {
            binding.tvSenderName.text = message.senderName
            binding.tvTime.text = DateUtils.formatTimestampFull(message.timestamp)

            // Si es una imagen se carga con Glide desde la URL de Storage, si no se muestra el texto
            if (message.type == Constants.MESSAGE_TYPE_IMAGE) {
                binding.ivMessageImage.visibility = View.VISIBLE
                binding.tvMessageText.visibility = View.GONE
                Glide.with(binding.root.context)
                    .load(message.imageUrl)
                    .into(binding.ivMessageImage)
            } else {
                binding.ivMessageImage.visibility = View.GONE
                binding.tvMessageText.visibility = View.VISIBLE
                binding.tvMessageText.text = message.text
            }

            // Mis mensajes van a la derecha y con otro color
            if (message.senderId == currentUserId) {
                binding.messageContainer.gravity = Gravity.END
                binding.bubble.setBackgroundResource(R.drawable.bg_message_sent)
            } else {
                binding.messageContainer.gravity = Gravity.START
                binding.bubble.setBackgroundResource(R.drawable.bg_message_received)
            }
        }
    }

    // Crea la vista de cada mensaje a partir del XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    // Pasa el mensaje de esa posición a su vista
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    // Devuelve cuántos mensajes hay en la lista
    override fun getItemCount(): Int = messages.size
}
