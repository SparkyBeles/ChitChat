package com.example.chitchat.Model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val friends: MutableList<String> = mutableListOf()
)