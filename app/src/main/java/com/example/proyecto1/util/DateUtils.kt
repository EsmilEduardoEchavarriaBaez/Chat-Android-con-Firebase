package com.example.proyecto1.util

import java.text.SimpleDateFormat
import java.util.*


object DateUtils {


    // Convierte la hora en milisegundos al formato HH:mm
    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }

    // Convierte la hora en milisegundos al formato dd/MM/yyyy HH:mm
    fun formatTimestampFull(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return format.format(date)
    }


}