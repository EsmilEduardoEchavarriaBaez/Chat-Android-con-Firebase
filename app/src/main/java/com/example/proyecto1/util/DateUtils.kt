package com.example.proyecto1.util

import java.text.SimpleDateFormat
import java.util.*


object DateUtils {

    // Los formatos se crean una sola vez y se reutilizan, en vez de crear uno nuevo por cada mensaje
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("es", "DO"))
    private val dayFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "DO"))

    // Convierte la hora en milisegundos al formato HH:mm
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    // Devuelve "Hoy", "Ayer" o la fecha dd/MM/yyyy para separar los mensajes por día
    fun formatDay(timestamp: Long): String {
        // Se compara la fecha del mensaje con la de hoy y la de ayer
        val day = dayFormat.format(Date(timestamp))
        val today = dayFormat.format(Date())
        val yesterday = dayFormat.format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
        return when (day) {
            today -> "Hoy"
            yesterday -> "Ayer"
            else -> day
        }
    }

    // Revisa si dos horas en milisegundos caen en el mismo día
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        return dayFormat.format(Date(timestamp1)) == dayFormat.format(Date(timestamp2))
    }


}