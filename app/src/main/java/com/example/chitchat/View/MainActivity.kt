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
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.chitchat.R
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    lateinit var launcher: ActivityResultLauncher<Intent>
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

        vm = ViewModelProvider(this).get(
            ChatViewModel::
            class.java
        )

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

        binding.GoogleBtn?.setOnClickListener() {
signInWithGoogle()
        }
        binding.signUpBtn.setOnClickListener()
        {
            vm.activeFragment.value =
                "SignUpFragment" // vm help remeber fragment in land or standing mode

            showSignUpFragment()
        }



        binding.signInBtn.setOnClickListener()
        {
            vm.activeFragment.value =
                "SignInFragment" // vm help remeber fragment in land or standing mode

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
        if (auth.currentUser != null) {
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
            val chatIntent = Intent(this, ChatActivity::class.java)
            startActivity(chatIntent)
        }
    }

    fun showSignInFragment() {


        val existingFragment = supportFragmentManager.findFragmentByTag("SignInFragment")
      //  if (existingFragment == null) {
            val signInFragment = SignInFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.authFrame, signInFragment, "SignInFragment")
                .addToBackStack(null)
                .commit()
switchToSignUpButton()
    //   }

    }

    fun showSignUpFragment() {
        val existingFragment = supportFragmentManager.findFragmentByTag("SignUpFragment")
     //  if (existingFragment == null) {
            val signUpFragment = SignUpFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.authFrame, signUpFragment, "SignUpFragment")
                .addToBackStack(null)
                .commit()
        switchToSignInButton()
      //  }


    }
fun switchToSignUpButton() {
    binding.signInBtn.visibility = View.GONE
    binding.signUpBtn.visibility = View.VISIBLE
}
    fun switchToSignInButton() {
        binding.signInBtn.visibility = View.VISIBLE
        binding.signUpBtn.visibility = View.GONE

    fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, gso)

    }

    fun signInWithGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("!!!", "Google auth success")
            } else {
                Log.d("!!!", "Google auth failed")
            }
        }

    }
}

