package com.example.proyecto1.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto1.databinding.ItemUserBinding
import com.example.proyecto1.model.User

// ListAdapter compara la lista vieja con la nueva y solo redibuja los usuarios que cambiaron
class UserAdapter(
    private val onUserClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        // Muestra el nombre y el correo del usuario y detecta el toque
        fun bind(user: User) {
            // El XML usa Data Binding (@{user.name}), así que se le pasa el usuario al binding
            binding.user = user
            binding.executePendingBindings()
            // Al tocar el usuario se avisa a la pantalla para que abra su chat
            binding.root.setOnClickListener {
                onUserClick(user)
            }
        }
    }

    // Crea la vista de cada usuario a partir del XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    // Pasa el usuario de esa posición a su vista
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        // Revisa si dos usuarios son el mismo usando su uid
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        // Revisa si los datos del usuario cambiaron
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
