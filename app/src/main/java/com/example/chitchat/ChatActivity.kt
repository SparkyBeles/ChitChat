package com.example.chitchat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chitchat.databinding.ActivityChatBinding
import com.example.chitchat.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var chatButton: Button
    lateinit var friendsButton: Button
    lateinit var signOutButton: Button
    var auth: FirebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)



        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        chatButton = findViewById(R.id.chatButton)

        friendsButton = findViewById(R.id.friendsButton)
        signOutButton = findViewById(R.id.signOutButton)
        friendsButton.setOnClickListener {
            showFriendsListFragment()
        }

        chatButton.setOnClickListener {
            showChatFragment()
        }
        signOutButton.setOnClickListener {
            signOut()
        }

    }



    fun showFriendsListFragment() {
        val friendsListFragment = FriendsFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.friendsOrChat, friendsListFragment, "Friends")
        transaction.commit()
    }

    fun showChatFragment() {
        val chatFragment = ChatFragment()
        val bundle = Bundle()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.friendsOrChat, chatFragment, "Chat")
        transaction.commit()
    }

    fun signOut() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        if (auth.currentUser == null) {
            Toast.makeText(this, "You're signed out", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
