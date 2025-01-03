package com.example.chitchat

data class Message (
    val id: String = "",
    val senderId: String = "",
    val recieverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)