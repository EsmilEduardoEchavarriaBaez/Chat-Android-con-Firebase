package com.example.proyecto1.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto1.databinding.ItemUserBinding
import com.example.proyecto1.model.User

class UserAdapter(


    private val users: List<User>,
    private val onUserClick: (User) -> Unit

         ) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        // Muestra el nombre y el correo del usuario y detecta el toque
        fun bind(user: User) {
            // El XML usa Data Binding (@{user.name}), así que se le pasa el usuario al binding
            binding.user = user
            binding.executePendingBindings()
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
        holder.bind(users[position])
    }

    // Devuelve cuántos usuarios hay en la lista
    override fun getItemCount(): Int = users.size
}