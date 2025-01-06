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
    var auth: FirebaseAuth = Firebase.auth
    lateinit var vm : ChatViewModel

    private var _binding: FragmentSignInBinding? =
        null
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

        vm = ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)

        vm.startChat.observe(viewLifecycleOwner) { startChat ->
            if(startChat) {
                vm.startChat.value = false
            }
        }

        var signInBtn = binding.signInFragmentBtn
        signInBtn.setOnClickListener {
            signIn()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun signIn() {
        val email = binding.emailEt1?.text.toString()
        val password = binding.passwordEt1?.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    vm.startChat.value = true
//                    Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
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