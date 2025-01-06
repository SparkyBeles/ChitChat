package com.example.chitchat.Model

data class Message (
    val id: String = "",
    val senderId: String = "",
    val recieverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)