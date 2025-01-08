package com.example.chitchat.View

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.CONSUMED
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.example.chitchat.R
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.ActivityChatBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var vm: ChatViewModel
    lateinit var chatButton: Button
    lateinit var friendsButton: Button
    lateinit var signOutButton: Button
    var auth: FirebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)



        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener( // Attention!
            window.decorView
        ) { v, insets ->
            val windowInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            findViewById<View>(R.id.main).updatePadding(bottom = windowInsets.bottom)

            return@setOnApplyWindowInsetsListener CONSUMED //whole ViewCompat has been changed for bottomnavbar
        }
        showFriendsListFragment()

        val bottomnav = findViewById<BottomNavigationView>(R.id.bottomNavigationView) //binding doesn't work
        bottomnav.setOnItemSelectedListener { item ->
            when(item.itemId) {

                R.id.person -> {
                    showFriendsListFragment()
                    vm.activeFragment2.value = "Friends" // added for rotation reminding
                    true
                }
                R.id.profile -> {
                    vm.activeFragment2.value = "Profile" // added for rotation reminding
                    showProfileFragment()

                    true
                }
                else -> false
            }
        }
        // Observe active fragment
        vm.activeFragment2.observe(this) { fragmentTag ->
            when (fragmentTag) {
                "Friends" -> showFriendsListFragment()
                "Profile" -> showProfileFragment()
            }
        }

        // Initilize fragment from start
        if (savedInstanceState == null) {
            vm.activeFragment2.value = "Friends" // default fragment
        }
//        chatButton = findViewById(R.id.chatButton)
//
//        friendsButton = findViewById(R.id.friendsButton)
//        signOutButton = findViewById(R.id.signOutButton)
//        friendsButton.setOnClickListener {
//            showFriendsListFragment()
//        }
//
//        chatButton.setOnClickListener {
//            showChatFragment()
//        }
//        signOutButton.setOnClickListener {
//            signOut()
//        }


    }


fun showProfileFragment() {
    val profileFragment = ProfileFragment()
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.friendsOrChat, profileFragment, "Profile")
    transaction.addToBackStack(null)
    transaction.commit()
}
    fun showFriendsListFragment() {
        val existingFragment = supportFragmentManager.findFragmentByTag("Friends")
       // if (existingFragment == null) {
            val friendsListFragment = FriendsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.friendsOrChat, friendsListFragment, "Friends")
                .addToBackStack(null)
                .commit()
      //  }
    }

    fun showChatFragment() {
        val chatFragment = ChatFragment()
        val bundle = Bundle()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.friendsOrChat, chatFragment, "Chat")
        transaction.addToBackStack(null)
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
