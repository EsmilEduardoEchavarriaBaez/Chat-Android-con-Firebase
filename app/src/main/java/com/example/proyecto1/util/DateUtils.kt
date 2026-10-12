package com.example.proyecto1.util

import java.text.SimpleDateFormat
import java.util.*


object DateUtils {

    // El formato se crea una sola vez y se reutiliza, en vez de crear uno nuevo por cada mensaje
    private val fullFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "DO"))

    // Convierte la hora en milisegundos al formato dd/MM/yyyy HH:mm
    fun formatTimestampFull(timestamp: Long): String {
        return fullFormat.format(Date(timestamp))
    }


}