package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.chitchat.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth // Not sure we'll need auth in main though
    lateinit var vm : ChatViewModel

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


        //val db = Firebase.firestore // flyttad till firebasemanager

        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        showSignInFragment()
        binding.signUpBtn.setOnClickListener {
            showSignUpFragment()
        }


        binding.signInBtn.setOnClickListener {
            showSignInFragment()
        }

        vm.startChat.observe(this) { startChat ->
            if(startChat) {
                val chatIntent = Intent(this, ChatActivity::class.java)
                startActivity(chatIntent)
                vm.startChat.value = false
            }
        }
    }

    fun showSignInFragment() {
        val signInFragment = SignInFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.authFrame, signInFragment, "SignInFragment")
        transaction.commit()
    }

    fun showSignUpFragment() {
        val signUpFragment = SignUpFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.authFrame, signUpFragment, "SignUpFragment")
        transaction.commit()
    }

}

