package com.example.proyecto1.ui.users


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
import com.example.proyecto1.viewmodel.UsersViewModel
class UsersActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityUsersBinding
    private lateinit var userAdapter: UserAdapter
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
        showCurrentUser()
        loadUsers()
        setupLogoutButton()
        setupNotifications()


    }

    // Pone el nombre del usuario que inició sesión como título (llega junto con la lista de usuarios)
    private fun showCurrentUser() {
        viewModel.currentUserName.observe(this) { name ->
            binding.tvCurrentUser.text = name
        }
    }

    // Guarda el token, escucha los mensajes nuevos y pide el permiso de notificaciones
    private fun setupNotifications() {
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
        // El adaptador se crea una sola vez y después solo se le pasan los datos nuevos
        userAdapter = UserAdapter { user ->
            openChat(user)
        }
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        // La lista no cambia de tamaño con su contenido, así se evitan cálculos de más
        binding.rvUsers.setHasFixedSize(true)
        binding.rvUsers.adapter = userAdapter
    }

    // Carga los usuarios y los muestra en la lista
    private fun loadUsers() {
        viewModel.loadUsers()
        viewModel.usersList.observe(this) { state ->
            when (state) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    userAdapter.submitList(state.data)
                }
                is Resource.Error -> {
                    showLoading(false)
                    binding.tvError.text = state.exception.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    // Abre el chat con el usuario elegido pasándole su uid, su nombre y el mío
    private fun openChat(user: User) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_ID, user.uid)
        intent.putExtra(Constants.EXTRA_USER_NAME, user.name)
        // Mi nombre ya se descargó con la lista, así el chat no tiene que volver a pedirlo a Firebase
        intent.putExtra(Constants.EXTRA_CURRENT_USER_NAME, viewModel.currentUserName.value)
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
