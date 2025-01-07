package com.example.chitchat.Model

data class Message (
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val receiverName : String = "",
    val message: String = "",
    val timestamp: Long = 0L
)