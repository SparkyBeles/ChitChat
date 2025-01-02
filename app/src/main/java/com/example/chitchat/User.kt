package com.example.chitchat

data class User(val name:String, val id:Int) {
    constructor(): this("",0)
}