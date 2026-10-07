package com.example.proyecto1.util


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream


object ImageUtils {

    //Convierte un bitmap a string base64
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 80): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


    //Convierte de string de base64 a bitmap
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            null
        }
    }
    //Obtener imagen mediante camara o galeria
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }
//comprime un bitmap
    fun compressBitmap(bitmap: Bitmap, maxWidth: Int = 500, maxHeight: Int = 500): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaledWidth = if (width > maxWidth) maxWidth else width
        val scaledHeight = if (height > maxHeight) maxHeight else height

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    }


    fun uriToBase64(context: Context, uri: Uri, quality: Int = 70): String? {
        return try {
            val bitmap = uriToBitmap(context, uri) ?: return null
            val compressed = compressBitmap(bitmap)
            bitmapToBase64(compressed, quality)
        } catch (e: Exception) {
            null
        }
    }

    fun getBase64SizeInKB(base64String: String): Double {
        return (base64String.length * 0.75) / 1024
    }







}