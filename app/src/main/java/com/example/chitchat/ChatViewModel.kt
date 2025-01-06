package com.example.chitchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {



    val startChat = MutableLiveData<Boolean>()
    private val firebaseManager = FirebaseManager()
    val friends : LiveData<List<User>> = firebaseManager.getFriends()

    val activeFragment = MutableLiveData<String>("SignInFragment")
    val activeFragment2= MutableLiveData<String>("Friends") // Default fragment



}