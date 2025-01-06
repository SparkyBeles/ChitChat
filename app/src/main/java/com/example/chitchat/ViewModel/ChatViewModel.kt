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
    val activeFragment = MutableLiveData<String>("SignInFragment")
    val activeFragment2= MutableLiveData<String>("Friends") // Default fragment



    // Livedata for chat collection ID. When updated, ChatFragment is opened from FriendsFragment.
    private val _chatCollectionId = MutableLiveData<String?>()
    val chatCollectionId: LiveData<String?> = _chatCollectionId


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

    // Function that calls createChatCollectionIfNeeded in FirebaseManager.
    // Takes in the IDs for both current user and friend.
    // The callback function sets/updates the chatCollectionId LiveData (observed in FriendsFragment).
    fun openChat(friendId: String, currentUserId: String) {
        firebaseManager.createChatCollectionIfNeeded(friendId, currentUserId) { chatCollectionId ->
            _chatCollectionId.postValue(chatCollectionId)
        }
    }

}