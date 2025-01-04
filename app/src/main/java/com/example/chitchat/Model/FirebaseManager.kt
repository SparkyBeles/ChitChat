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



    fun getFriends(userId: String, callback: (List<User>) -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val friendIds = document.get("friends") as? List<String> ?: emptyList()
                    Log.d("Firebase", "Friend IDs: $friendIds")

                    if (friendIds.isEmpty()) {
                        callback(emptyList())
                        return@addOnSuccessListener
                    }

                    db.collection("users")
                        .whereIn("id", friendIds)
                        .get()
                        .addOnSuccessListener { result ->
                            val friends = result.documents.mapNotNull { doc ->
                                doc.toObject(User::class.java)
                            }
                            Log.d("Firebase", "Fetched friends: $friends")
                            callback(friends)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error fetching friend details", e)
                            callback(emptyList())
                        }
                } else {
                    Log.e("Firebase", "No document found for user: $userId")
                    callback(emptyList())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching user document", e)
                callback(emptyList())
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
}