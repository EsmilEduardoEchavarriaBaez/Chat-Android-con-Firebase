package com.example.proyecto1.repository

import android.net.Uri
import com.example.proyecto1.model.Resource
import com.example.proyecto1.util.Constants
import com.google.firebase.storage.FirebaseStorage

class StorageRepository {

    private val storage = FirebaseStorage.getInstance().reference

    // Sube la imagen a Firebase Storage y devuelve su URL de descarga
    fun uploadImage(chatId: String, imageUri: Uri, onResult: (Resource<String>) -> Unit) {
        // Cada imagen se guarda en chat_images/{chatId}/ con la hora como nombre
        val imageRef = storage.child(Constants.CHAT_IMAGES_FOLDER)
            .child(chatId)
            .child("${System.currentTimeMillis()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Cuando termina de subir, se pide la URL de descarga
                imageRef.downloadUrl
                    .addOnSuccessListener { url ->
                        onResult(Resource.Success(url.toString()))
                    }
                    .addOnFailureListener { exception ->
                        onResult(Resource.Error(exception))
                    }
            }
            .addOnFailureListener { exception ->
                onResult(Resource.Error(exception))
            }
    }
}
