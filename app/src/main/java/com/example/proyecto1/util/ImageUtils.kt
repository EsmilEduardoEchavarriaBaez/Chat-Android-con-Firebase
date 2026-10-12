package com.example.proyecto1.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import java.io.ByteArrayOutputStream

object ImageUtils {

    // Tamaño máximo en píxeles del lado más largo de la imagen que se sube
    private const val MAX_IMAGE_SIZE = 1280

    // Calidad del JPEG, de 0 a 100
    private const val JPEG_QUALITY = 75

    // Reduce y comprime la imagen elegida para que pese mucho menos antes de subirla (devuelve null si falla)
    fun compressImage(context: Context, imageUri: Uri): ByteArray? {
        // Glide achica la imagen al leerla y la endereza si la foto venía girada.
        // No se guarda en caché porque solo se usa una vez para subirla
        val request = Glide.with(context)
            .asBitmap()
            .load(imageUri)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .submit(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)

        return try {
            val bitmap = request.get()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            stream.toByteArray()
        } catch (e: Exception) {
            Log.e("ImageUtils", "No se pudo comprimir la imagen: ${e.message}")
            null
        } finally {
            // Se libera el bitmap para que Glide pueda reutilizar esa memoria
            Glide.with(context).clear(request)
        }
    }
}
