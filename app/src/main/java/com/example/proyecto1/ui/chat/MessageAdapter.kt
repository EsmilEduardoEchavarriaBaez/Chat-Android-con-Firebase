package com.example.proyecto1.ui.chat

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ItemMessageBinding
import com.example.proyecto1.model.Message
import com.example.proyecto1.util.DateUtils

class MessageAdapter(
    private val currentUserId: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages: List<Message> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvSenderName.text = message.senderName
            binding.tvMessageText.text = message.text
            binding.tvTime.text = DateUtils.formatTimestampFull(message.timestamp)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size
}
