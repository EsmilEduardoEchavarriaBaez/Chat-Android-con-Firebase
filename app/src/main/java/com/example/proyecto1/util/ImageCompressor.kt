package com.example.proyecto1.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import java.io.ByteArrayOutputStream

object ImageCompressor {

    // Tamaño máximo en píxeles del lado más largo de la imagen que se sube
    private const val MAX_IMAGE_SIZE = 1280

    // Calidad del JPEG, de 0 a 100
    private const val JPEG_QUALITY = 75

    // Reduce y comprime la imagen elegida para que pese mucho menos antes de subirla (devuelve null si falla)
    fun compressImage(context: Context, imageUri: Uri): ByteArray? {
        // Glide lee la imagen ya achicada y enderezada, sin guardarla en caché porque solo se usa una vez
        val request = Glide.with(context)
            .asBitmap()
            .load(imageUri)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .submit(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)

        return try {
            // Espera la imagen y la comprime a JPEG en memoria
            val bitmap = request.get()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            stream.toByteArray()
        } catch (e: Exception) {
            // Si algo falla se anota en Logcat y se devuelve null
            Log.e("ImageCompressor", "No se pudo comprimir la imagen: ${e.message}")
            null
        } finally {
            // Se libera el bitmap para que Glide pueda reutilizar esa memoria
            Glide.with(context).clear(request)
        }
    }
}
