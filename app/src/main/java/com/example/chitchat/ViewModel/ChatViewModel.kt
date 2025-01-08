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
    val addFriendStatus = MutableLiveData<Boolean>()
    val activeFragment = MutableLiveData<String>("SignInFragment")
    val activeFragment2= MutableLiveData<String>("Friends") // Default fragment

    // Livedata for chat collection ID. When updated, ChatFragment is opened from FriendsFragment.
    private val _chatCollectionId = MutableLiveData<String?>()
    val chatCollectionId: LiveData<String?> = _chatCollectionId

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun fetchCurrentUser(userId: String) {
        firebaseManager.getCurrentUser(userId) { user ->
            _currentUser.postValue(user)
        }
    }

    fun getAllMessages(chatCollectionId: String) {
        firebaseManager.getAllMessages(chatCollectionId) { messages ->
            _messages.postValue(messages)
        }
    }

    // Function to send a message in ChatFragment.
    fun sendMessage(chatCollectionId: String, message: Message, callback: (Boolean) -> Unit) {
        firebaseManager.sendMessage(chatCollectionId, message) { success ->
            callback(success)
        }
    }

    //Calls firebasemanager to get friends and returns a liveData of friends
    fun loadFriends(userId: String): LiveData<List<User>> {
        val friendsLiveData = MutableLiveData<List<User>>()

        firebaseManager.getFriends(userId) { friends ->
            friendsLiveData.value = friends
        }
        return friendsLiveData
    }

    //Calls firebasemanager to add a friend with currentUserId and email
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