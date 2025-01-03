package com.example.chitchat

import android.icu.util.Currency
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {

    val startChat = MutableLiveData<Boolean>()
    private val firebaseManager = FirebaseManager()
    val friends : LiveData<List<User>> = MutableLiveData()
    val messages = MutableLiveData<List<Message>>()
    val addFriendStatus = MutableLiveData<Boolean>()


    fun loadFriends(userId: String): LiveData<List<User>> {
        val friendsLiveData = MutableLiveData<List<User>>()


        firebaseManager.getFriends(userId) { friends ->
            friendsLiveData.value = friends
        }
        return friendsLiveData
    }

    fun addFriend(currentUserId: String, email: String) {
        firebaseManager.addFriend(currentUserId, email){ success ->
            addFriendStatus.value = success
        }
    }

}