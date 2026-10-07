package com.example.proyecto1.ui.users


import com.example.proyecto1.R
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto1.databinding.ActivityUsersBinding
import com.example.proyecto1.model.Resource
import com.example.proyecto1.model.User
import com.example.proyecto1.ui.auth.LoginActivity
import com.example.proyecto1.ui.chat.ChatActivity
import com.example.proyecto1.util.Constants
import com.example.proyecto1.util.NotificationUtils
import com.example.proyecto1.viewmodel.UsersViewModel
class UsersActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityUsersBinding
    private val viewModel: UsersViewModel by viewModels()

    // Pide el permiso de notificaciones (solo hace falta desde Android 13)
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }



    // Prepara la pantalla de usuarios
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadUsers()
        setupLogoutButton()
        setupNotifications()


    }

    // Crea el canal, guarda el token, escucha los mensajes nuevos y pide el permiso de notificaciones
    private fun setupNotifications() {
        NotificationUtils.createChannel(this)
        viewModel.saveFcmToken()
        viewModel.startNotifications()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    // Configura la lista de usuarios
    private fun setupRecyclerView()
    {
        binding.rvUsers.layoutManager = LinearLayoutManager(this)




    }

    // Carga los usuarios y los muestra en la lista
    private fun loadUsers() {
        viewModel.loadUsers()
        viewModel.usersList.observe(this) { state ->
            when (state) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    val adapter = UserAdapter(state.data) { user ->
                        openChat(user)
                    }
                    binding.rvUsers.adapter = adapter
                }
                is Resource.Error -> {
                    showLoading(false)
                    binding.tvError.text = state.exception.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    // Abre el chat con el usuario elegido pasándole su uid y su nombre
    private fun openChat(user: User) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_ID, user.uid)
        intent.putExtra(Constants.EXTRA_USER_NAME, user.name)
        startActivity(intent)
    }

    // Cierra la sesión y vuelve al login
    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Muestra u oculta la barra de carga
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}
