package com.example.chitchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class FirebaseManager {
    private val db = Firebase.firestore

    lateinit var currentUser: FirebaseUser

    private val _user = MutableLiveData(mutableListOf<User>())

    val user: LiveData<MutableList<User>> get() = _user
   init{
      addSnapchotListener()
   }
    fun addSnapchotListener(){
        currentUser = Firebase.auth.currentUser ?: return
        db.collection("user").document(currentUser.uid).collection("users").addSnapshotListener { snapshot, error ->
            if(snapshot != null){
                val currentList = mutableListOf<User>()
                for(doc in snapshot.documents){
                    val user = doc.toObject<User>()
                    if( user!= null){
                        currentList.add(user)
                    }
                }
                _user.value = currentList
            }
        }

    }

    fun addUser(name:String,id:Int){
        val user = User(name,id)
    }
}