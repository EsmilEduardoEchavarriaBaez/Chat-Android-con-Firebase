package com.example.projecto1.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


object DateUtils {


    fun formatTimestamp(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }

    fun formatTimestampFull(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return format.format(date)
    }


}