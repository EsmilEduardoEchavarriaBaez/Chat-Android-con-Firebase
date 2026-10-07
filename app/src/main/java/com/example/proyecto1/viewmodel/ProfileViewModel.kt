package com.example.proyecto1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto1.model.Resource
import com.example.proyecto1.model.User
import com.example.proyecto1.repository.UserRepository

class ProfileViewModel : ViewModel (){

    private val repository = UserRepository()
    private val _profileState = MutableLiveData<Resource<User>>()
    val profileState: LiveData<Resource<User>> = _profileState

    private val _updatePhotoState = MutableLiveData<Resource<Unit>>()
    val updatePhotoState: LiveData<Resource<Unit>> = _updatePhotoState

    fun loadCurrentUserProfile() {
        _profileState.value = Resource.Loading
        val uid = repository.getCurrentUser() ?: run {
            _profileState.value = Resource.Error(Exception("Usuario no identificado"))
            return
        }

        repository.getUserById(uid) { result ->
            _profileState.value = result
        }
    }

    fun updateProfileImage(base64Image: String) {
        _updatePhotoState.value = Resource.Loading
        val uid = repository.getCurrentUser() ?: run {
            _updatePhotoState.value = Resource.Error(Exception("Usuario no identificado"))
            return
        }

        repository.updateProfileImage(uid, base64Image) { result ->
            _updatePhotoState.value = result
        }
    }





}