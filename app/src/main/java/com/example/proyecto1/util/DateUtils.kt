package com.example.proyecto1.util

import java.text.SimpleDateFormat
import java.util.*


object DateUtils {

    // Los formatos se crean una sola vez y se reutilizan, en vez de crear uno nuevo por cada mensaje
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("es", "DO"))
    private val fullFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "DO"))

    // Convierte la hora en milisegundos al formato HH:mm
    fun formatTimestamp(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    // Convierte la hora en milisegundos al formato dd/MM/yyyy HH:mm
    fun formatTimestampFull(timestamp: Long): String {
        return fullFormat.format(Date(timestamp))
    }


}