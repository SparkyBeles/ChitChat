package com.example.chitchat.Model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class FirebaseManager {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

//    private val _users = MutableLiveData(mutableListOf<User>())
//    val users: LiveData<MutableList<User>> get() = _users

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser



    init{
        addSnapshotListenerForCurrentUser()
    }

    fun addSnapshotListenerForCurrentUser(){
        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            Log.e("Firebase", "No user logged in")
            return
        }

        db.collection("users").document(firebaseUser.uid)
            .addSnapshotListener{ snapshot, e ->
                if (e != null){
                    Log.e("Firebase", "User not found $e")
                    return@addSnapshotListener
                }
                snapshot?.toObject<User>()?.let { user ->
                    _currentUser.postValue(user)
                }
            }

    }

    fun saveNewUser(user: User){
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.i("Firebase", "User added successfully")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to add user")
            }
    }



//    fun getFriends(userId: String, callback: (List<User>) -> Unit) {
//        db.collection("users")
//            .document(userId)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document != null && document.exists()) {
//                    val friendIds = document.get("friends") as? List<String> ?: emptyList()
//                    Log.d("Firebase", "Friend IDs: $friendIds")
//
//                    if (friendIds.isEmpty()) {
//                        callback(emptyList())
//                        return@addOnSuccessListener
//                    }
//
//                    db.collection("users")
//                        .whereIn("id", friendIds)
//                        .get()
//                        .addOnSuccessListener { result ->
//                            val friends = result.documents.mapNotNull { doc ->
//                                doc.toObject(User::class.java)
//                            }
//                            Log.d("Firebase", "Fetched friends: $friends")
//                            callback(friends)
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("Firebase", "Error fetching friend details", e)
//                            callback(emptyList())
//                        }
//                } else {
//                    Log.e("Firebase", "No document found for user: $userId")
//                    callback(emptyList())
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firebase", "Error fetching user document", e)
//                callback(emptyList())
//            }
//    }

    fun getFriends(userId: String, callback: (List<User>) -> Unit) {
        db.collection("users")
            .document(userId)
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    Log.e("Firebase", "Error fetching user document", exception)
                    callback(emptyList())
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val friendIds = documentSnapshot.get("friends") as? List<String> ?: emptyList()
                    Log.d("Firebase", "Friend IDs: $friendIds")

                    if (friendIds.isEmpty()) {
                        callback(emptyList())
                        return@addSnapshotListener
                    }

                    db.collection("users")
                        .whereIn("id", friendIds)
                        .addSnapshotListener { querySnapshot, exception ->
                            if (exception != null) {
                                Log.e("Firebase", "Error fetching friend details", exception)
                                callback(emptyList())
                                return@addSnapshotListener
                            }

                            val friends = querySnapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(User::class.java)
                            } ?: emptyList()
                            Log.d("Firebase", "Fetched friends: $friends")
                            callback(friends)
                        }
                } else {
                    Log.e("Firebase", "No document found for user: $userId")
                    callback(emptyList())
                }
            }
    }



    fun addFriend(currentUserId: String, email:String, onFriendAdded: (Boolean) -> Unit){
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty){
                    onFriendAdded(false)
                    return@addOnSuccessListener
                }

                val friendDoc = result.documents.first()
                val friendId = friendDoc.id

                val currentUserRef = db.collection("users").document(currentUserId)
                val friendRef = db.collection("users").document(friendId)

                currentUserRef.update("friends", FieldValue.arrayUnion(friendId))
                friendRef.update("friends", FieldValue.arrayUnion(currentUserId))

                onFriendAdded(true)
            }
            .addOnFailureListener {
                onFriendAdded(false)
            }
    }


    // Creating a chat collection with unique ID for currentUser and another user if one doesn't already exist.
    // Takes in friendId and currentUserId and has a callback function that returns the chat collection ID to ChatFragment.
    fun createChatCollectionIfNeeded(friendId: String, currentUserId: String?, callback: (String?) -> Unit) {

        // Calls the helper function to create the chat collection ID.
        val chatCollectionId = createChatCollectionId(friendId, currentUserId ?: "")

        // Creating a reference to the chat collection.
        db.collection("chats").document(chatCollectionId)
            .get()
            .addOnSuccessListener { document ->
                // Checks if the chat collection already exists, if it does, returns the chat collection ID.
                if(document != null && document.exists()) {
                    callback(chatCollectionId)
                } else {
                    // Chat collection does not exist, create it and return the chat collection ID.
                    db.collection("chats").document(chatCollectionId)
                        .set(mapOf("users" to listOf(friendId, currentUserId)))
                        .addOnSuccessListener {
                            Log.d("CHAT", "Chat collection created successfully")
                            callback(chatCollectionId)
                        }
                        .addOnFailureListener { e ->
                            Log.e("CHAT", "Error creating chat collection", e)
                            callback(null) // Returns null if error.
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("CHAT", "Error checking chat collection", e)
                callback(null) // Returns null if error.
            }
    }


    // Creates a unique chat collection ID for two users based on their IDs.
    private fun createChatCollectionId(userId1 : String, userId2 : String) : String {
        return if (userId1 < userId2) {
            "$userId1-$userId2"
        } else {
            "$userId2-$userId1"
        }
    }


}