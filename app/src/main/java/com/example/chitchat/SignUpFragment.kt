package com.example.chitchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.chitchat.databinding.FragmentSignInBinding
import com.example.chitchat.databinding.FragmentSignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpFragment : Fragment() {
    var auth: FirebaseAuth = Firebase.auth

    private var _binding: FragmentSignUpBinding? =
        null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )
        var name = binding.userNameEd
        var signUpBtn = binding.signUpFragmentBtn
        signUpBtn.setOnClickListener {
            signUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun signUp() {
        val email = binding.emailEt.text.toString()
        val password = binding.passwordEt.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(context,"User is created!", Toast.LENGTH_SHORT).show()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.authFrame, SignInFragment())
                        .commit()


                } else {
                    Toast.makeText(context,"User not created", Toast.LENGTH_SHORT).show()
                    //   Log.d("!!!", "user not created ${task.exception}")
                }
            }
    }

}