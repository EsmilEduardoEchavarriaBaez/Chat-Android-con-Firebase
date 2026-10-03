package com.example.projecto1.model



data class Message(

        val id: String="",
        val senderId: String = "",
        val senderName: String = "",
        val text: String = "",
        val imageUrl: String = "",
        val timestamp: Long = System.currentTimeMillis() ,
        val type: String = "TEXT"




)
