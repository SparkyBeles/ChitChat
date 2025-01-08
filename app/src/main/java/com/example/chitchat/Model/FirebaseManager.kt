package com.example.chitchat.Model

import android.util.Log
import android.widget.Toast
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
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser


    init{
        addSnapshotListenerForCurrentUser()
    }

    fun addSnapshotListenerForCurrentUser(){
        //Get the currently logged in user from Firebase Auth
        val firebaseUser = auth.currentUser
        //check if a user is logged in and if not Log an error and return out of the function
        if (firebaseUser == null){
            Log.e("Firebase", "No user logged in")
            return
        }

        //Add a snapshot listener to see changes in the users document
        db.collection("users").document(firebaseUser.uid)
            .addSnapshotListener{ snapshot, e ->
                //if an error occurs log the error and return out of the callback function
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
        //Get the users ID from Firebase Auth and return out of the function if no user is logged in
        val userId = auth.currentUser?.uid ?: return

        //Create new user document in Firestore
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
        //Adds a listener to check for changes in the users document
        db.collection("users")
            .document(userId)
            .addSnapshotListener { documentSnapshot, exception ->
                //handles exceptions while getting the user document
                if (exception != null) {
                    Log.e("Firebase", "Error fetching user document", exception)
                    callback(emptyList()) // Returns a empty list if an error occurs
                    return@addSnapshotListener
                }

                // Checking if the document exists and gets the friend list from the user document
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val friendIds = documentSnapshot.get("friends") as? List<String> ?: emptyList()
                    Log.d("Firebase", "Friend IDs: $friendIds")

                    // If no friends exists return empty list
                    if (friendIds.isEmpty()) {
                        callback(emptyList())
                        return@addSnapshotListener
                    }

                    //Get all the friend info from the friendID
                    db.collection("users")
                        .whereIn("id", friendIds)
                        .addSnapshotListener { querySnapshot, exception ->
                            //handles exceptions while getting the friend info
                            if (exception != null) {
                                Log.e("Firebase", "Error fetching friend details", exception)
                                callback(emptyList()) //returns a empty list if an error occurs
                                return@addSnapshotListener
                            }

                            // Map the document to User object
                            val friends = querySnapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(User::class.java)
                            } ?: emptyList()
                            Log.d("Firebase", "Fetched friends: $friends")
                            callback(friends) //return list of friends as user objects
                        }
                } else {
                    // If no documents found print a Log and return a empty list
                    Log.e("Firebase", "No document found for user: $userId")
                    callback(emptyList())
                }
            }
    }



    fun addFriend(currentUserId: String, email:String, onFriendAdded: (Boolean) -> Unit){
        //finding users in the users collection with the email field matching the email sent
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                //if result is empty = no user with that email exist
                if (result.isEmpty){
                    //Updates Boolean that friend could not be added
                    onFriendAdded(false)
                    return@addOnSuccessListener
                }

                //get the document of the first match found
                val friendDoc = result.documents.first()
                val friendId = friendDoc.id // Gets the found users ID from the document

                //References to the current users and the friends document in firestore
                val currentUserRef = db.collection("users").document(currentUserId)
                val friendRef = db.collection("users").document(friendId)

                // Adds friends ID to the current users friend list in firestore
                currentUserRef.update("friends", FieldValue.arrayUnion(friendId))
                //Adds the current users ID to the friends friend list in firestore
                friendRef.update("friends", FieldValue.arrayUnion(currentUserId))

                //Updates Boolean that friend has been added
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

    fun getCurrentUser(userId: String, callback: (User?) -> Unit){
        //Gets data from the userID document in users collection
        db.collection("users")
            .document(userId)
            .get()
            //if successfull convert document to and return a User class
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                callback(user)
            }
            .addOnFailureListener { e ->
                //if failed Log the error and return null
                Log.e("Firebase", "Error: $e")
                callback(null)
            }
    }

    fun updateUser(userId: String, name: String, callback: (Boolean) -> Unit) {
        //Create a map with name as key and the new value
        val updatedUser = mapOf(
            "name" to name,
        )

        //Updates the name field of the user with the assigned userID in the collection users
        db.collection("users")
            .document(userId)
            .update(updatedUser)
            .addOnSuccessListener {
                Log.d("Firebase", "Update successfull!")
                callback(true)
            }
            .addOnFailureListener { e ->
                //if failed Log the error and return null
                Log.e("Firebase", "Error: $e")
                callback(false)
            }
    }

    fun getAllMessages(chatCollectionId: String, callback: (List<Message>) -> Unit) {
        val messagesCollectionRef = db // Define messagesCollectionRef
            .collection("chats")
            .document(chatCollectionId ?: "")
            .collection("messages")

        messagesCollectionRef.orderBy("timestamp").addSnapshotListener { snapshot, exception ->
            if(exception != null) {
                return@addSnapshotListener
            }

            // Fetch new messages and add them to the list.
            // The callback function is called with the new messages.
            val newMessages = snapshot?.toObjects(Message::class.java) ?: emptyList()
            callback(newMessages)
        }
    }


    // Function to store message sent in ChatFragment in Firebase.
    // Callback function is called when message is sent, if true, message is sent, if false, fail.
    fun sendMessage(chatCollectionId: String, message: Message, callback: (Boolean) -> Unit) {
        db.collection("chats")
            .document(chatCollectionId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error sending message", e)
                callback(false)
            }
    }


}