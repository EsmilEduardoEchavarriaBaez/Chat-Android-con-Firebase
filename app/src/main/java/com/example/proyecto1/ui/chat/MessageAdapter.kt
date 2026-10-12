package com.example.proyecto1.ui.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ItemMessageBinding
import com.example.proyecto1.model.Message
import com.example.proyecto1.util.Constants
import com.example.proyecto1.util.DateUtils

// ListAdapter compara la lista vieja con la nueva y solo redibuja los mensajes que cambiaron
class MessageAdapter(
    private val currentUserId: String
) : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        // Radio de las esquinas de las imágenes (12dp pasado a píxeles)
        private val imageCornerRadius = (12 * binding.root.resources.displayMetrics.density).toInt()

        // Llena una burbuja con el nombre, el texto o la imagen y la hora
        fun bind(message: Message) {
            val isMine = message.senderId == currentUserId

            // Mi nombre no hace falta en mis propios mensajes
            binding.tvSenderName.text = message.senderName
            binding.tvSenderName.visibility =
                if (isMine || message.senderName.isEmpty()) View.GONE else View.VISIBLE
            binding.tvTime.text = DateUtils.formatTimestampFull(message.timestamp)

            // Si es una imagen se carga con Glide desde la URL de Storage, si no se muestra el texto
            if (message.type == Constants.MESSAGE_TYPE_IMAGE) {
                binding.ivMessageImage.visibility = View.VISIBLE
                binding.tvMessageText.visibility = View.GONE
                Glide.with(binding.root.context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.bg_image_placeholder)
                    .transform(CenterCrop(), RoundedCorners(imageCornerRadius))
                    .into(binding.ivMessageImage)
            } else {
                // La vista se recicla, así que se suelta la imagen que tenía antes para liberar memoria
                Glide.with(binding.root.context).clear(binding.ivMessageImage)
                binding.ivMessageImage.visibility = View.GONE
                binding.tvMessageText.visibility = View.VISIBLE
                binding.tvMessageText.text = message.text
            }

            // Mis mensajes van a la derecha y con otro color
            if (isMine) {
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
        holder.bind(getItem(position))
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        // Revisa si dos mensajes son el mismo usando su id
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        // Revisa si el contenido del mensaje cambió
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
