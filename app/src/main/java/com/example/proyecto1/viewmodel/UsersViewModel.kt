package com.example.proyecto1.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto1.model.Resource
import com.example.proyecto1.model.User
import com.example.proyecto1.repository.UserRepository


class UsersViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _usersList = MutableLiveData<Resource<List<User>>>()
    val usersList: LiveData<Resource<List<User>>> = _usersList

    // Pide la lista de usuarios al repositorio
    fun loadUsers() {
        _usersList.value = Resource.Loading
        repository.getAllUsersExceptCurrent { result ->
            _usersList.value = result
        }
    }

    // Guarda el token de notificaciones del usuario
    fun saveFcmToken() {
        repository.saveCurrentFcmToken()
    }

    // Cierra la sesión del usuario
    fun logout() {
        repository.logout()
    }

}