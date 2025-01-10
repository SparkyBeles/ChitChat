package com.example.chitchat.View
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.FragmentSignInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.auth

class SignInFragment : Fragment() {
    //  Firebase Authentication instance to handle user authentication processes.
    private var auth: FirebaseAuth = Firebase.auth

    //  ViewModel instance to manage chat related logic.
    private lateinit var vm : ChatViewModel


    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        //  Initialize the ViewModel to manage logic & logic before the view is created.
        vm = ViewModelProvider(requireActivity())[ChatViewModel::class.java]
        vm.startChat.observe(viewLifecycleOwner) { startChat ->
            if(startChat) {
                vm.startChat.value = false
            }
        }


        val signInBtn = binding.btnSigninFrag

        signInBtn.setOnClickListener {
            signIn()
        }
        return binding.root
    }

    //  Function for signing in with email & password.
    private fun signIn() {
        val email = binding.etEmailSignin.text.toString()
        val password = binding.etPasswordSignin.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    vm.startChat.value = true
                   Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                    auth.currentUser
                } else {
                    val exception = task.exception

                    when (exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(context, "Incorrect E-Mail or Password", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Login failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }


}