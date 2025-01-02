package com.example.chitchat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
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


}