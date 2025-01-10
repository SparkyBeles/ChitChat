package com.example.chitchat.View

import  android.content.Intent
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.chitchat.Model.FirebaseManager
import com.example.chitchat.Model.User
import com.example.chitchat.R
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var vm: ChatViewModel
    private val firebaseManager = FirebaseManager()

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
        vm = ViewModelProvider(this).get(
            ChatViewModel::
            class.java
        )
        //gets data from Google, if users data is correct and account != null, returns token that sends to firebase
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("!!!", "API problem")
            }
        }

        // Observe active fragment
        vm.activeFragment.observe(this)
        { fragmentTag ->
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

        binding.btnGoogle.setOnClickListener() {
            signInWithGoogle()
        }
        binding.btnSignUp.setOnClickListener()
        {
            vm.activeFragment.value =
                "SignUpFragment" // vm help remember fragment in land or standing mode

            showSignUpFragment()
        }

        binding.btnSignIn.setOnClickListener()
        {
            vm.activeFragment.value =
                "SignInFragment" // vm help remember fragment in land or standing mode

            showSignInFragment()
        }

        vm.startChat.observe(this)
        { startChat ->
            if (startChat) {
                val chatIntent = Intent(this, ChatActivity::class.java)
                chatIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(chatIntent)
                vm.startChat.value = false
            }
        }
        //checks if user is signed in upon open the app
        if (auth.currentUser != null) {
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
            val chatIntent = Intent(this, ChatActivity::class.java)
            startActivity(chatIntent)
        }
    }

    fun showSignInFragment() {
        val signInFragment = SignInFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_auth, signInFragment, "SignInFragment")
            .addToBackStack(null)
            .commit()
        switchToSignUpButton()
    }

    fun showSignUpFragment() {
        val signUpFragment = SignUpFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_auth, signUpFragment, "SignUpFragment")
            .addToBackStack(null)
            .commit()
        switchToSignInButton()
    }

    fun switchToSignUpButton() {
        binding.btnSignIn.visibility = View.GONE
        binding.btnSignUp?.visibility = View.VISIBLE
    }

    fun switchToSignInButton() {
        binding.btnSignIn.visibility = View.VISIBLE
        binding.btnSignUp?.visibility = View.GONE
    }

    //starts google auth page, deprecated, but another code didn't work at all
    fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, gso)

    }

    // launches google sign in page, then awaits users token and other information
    fun signInWithGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    //gets Google auth token and creates a firebase account with it.
//skips the process of creating a new user object and goes to ChatActivity is token is already in the system
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val currentUser = auth.currentUser
                val db = FirebaseFirestore.getInstance()
                val usersRef = db.collection("users")
                usersRef.document(currentUser!!.uid).get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            val user = User(
                                id = currentUser.uid ?: "",
                                name = currentUser.displayName ?: "",
                                email = currentUser.email ?: ""
                            )
                            firebaseManager.saveNewUser(user)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("!!!", "Problem: $exception")
                    }
                Log.d("!!!", "Google auth success")
                vm.startChat.value = true
            } else {
                Log.d("!!!", "Google auth failed")
            }
        }

    }
}


