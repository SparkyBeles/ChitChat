package com.example.chitchat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {

    val startChat = MutableLiveData<Boolean>()

}