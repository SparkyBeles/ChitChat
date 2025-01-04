package com.example.chitchat.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chitchat.Model.FirebaseManager
import com.example.chitchat.Model.Message
import com.example.chitchat.Model.User

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