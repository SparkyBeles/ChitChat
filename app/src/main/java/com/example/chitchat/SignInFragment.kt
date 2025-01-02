package com.example.chitchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.chitchat.databinding.FragmentSignInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
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
    ): View? {
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

        var email = binding.emailEt
        var password = binding.passwordEt
        var signInBtn = binding.signInFragmentBtn
        signInBtn.setOnClickListener {
            signIn()
            vm.startChat.value = true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun signIn() {
        val email = binding.emailEt.text.toString()
        val password = binding.passwordEt.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                } else {

//                    Toast.makeText(context, "Oh no :(", Toast.LENGTH_SHORT).show()
                }
            }
    }

}