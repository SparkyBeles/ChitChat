package com.example.chitchat.View

import android.os.Bundle
import android.view.View
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

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var vm: ChatViewModel


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(
            window.decorView
        ) { v, insets ->
            val windowInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            findViewById<View>(R.id.main).updatePadding(bottom = windowInsets.bottom)

            return@setOnApplyWindowInsetsListener CONSUMED //whole ViewCompat has been changed for bottomnavbar
        }
        showFriendsListFragment()

        val bottomnav = findViewById<BottomNavigationView>(R.id.bottom_navigation_view) //binding doesn't work
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
    }


    fun showProfileFragment() {
        val profileFragment = ProfileFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fcv_friends_or_chat, profileFragment, "Profile")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun showFriendsListFragment() {
        val friendsListFragment = FriendsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_friends_or_chat, friendsListFragment, "Friends")
            .addToBackStack(null)
            .commit()
    }
}
