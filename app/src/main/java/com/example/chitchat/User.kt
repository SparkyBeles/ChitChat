package com.example.chitchat

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val friends: MutableList<String> = mutableListOf()
)