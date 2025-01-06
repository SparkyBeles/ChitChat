package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.chitchat.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth // Not sure we'll need auth in main though
    lateinit var vm: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        //val db = Firebase.firestore // flyttad till firebasemanager

        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        // Observe active fragment
        vm.activeFragment.observe(this) { fragmentTag ->
            when (fragmentTag) {
                "SignInFragment" -> showSignInFragment()
                "SignUpFragment" -> showSignUpFragment()
            }
        }

        // Initiate first fragment when app starts
        if (savedInstanceState == null) {
            showSignInFragment() // Standard
            vm.activeFragment.value = "SignInFragment"
        }

        showSignInFragment()
        binding.signUpBtn.setOnClickListener {
            showSignUpFragment()
            vm.activeFragment.value = "SignUpFragment" // vm help remeber fragment in land or standing mode
        }


        binding.signInBtn.setOnClickListener {
            showSignInFragment()
            vm.activeFragment.value = "SignInFragment" // vm help remeber fragment in land or standing mode
        }


        vm.startChat.observe(this) { startChat ->
            if (startChat) {
                val chatIntent = Intent(this, ChatActivity::class.java)
                chatIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(chatIntent)
                vm.startChat.value = false
            }
        }
        if (auth.currentUser != null) {
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
            val chatIntent = Intent(this, ChatActivity::class.java)
            startActivity(chatIntent)
        }
    }

    fun showSignInFragment() {
        val existingFragment = supportFragmentManager.findFragmentByTag("SignInFragment")
        if (existingFragment == null) {
            val signInFragment = SignInFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.authFrame, signInFragment, "SignInFragment")
                .addToBackStack(null)
                .commit()
        }
    }

    fun showSignUpFragment() {
        val existingFragment = supportFragmentManager.findFragmentByTag("SignUpFragment")
        if (existingFragment == null) {
            val signUpFragment = SignUpFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.authFrame, signUpFragment, "SignUpFragment")
                .addToBackStack(null)
                .commit()
        }
    }

}

