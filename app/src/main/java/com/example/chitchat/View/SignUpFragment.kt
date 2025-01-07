package com.example.chitchat.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chitchat.Model.FirebaseManager
import com.example.chitchat.R
import com.example.chitchat.Model.User
import com.example.chitchat.databinding.FragmentSignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpFragment : Fragment() {
    var auth: FirebaseAuth = Firebase.auth
    private val firebaseManager = FirebaseManager()

    private var _binding: FragmentSignUpBinding? = null
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
        val name = binding.userNameEd1?.text.toString()
        val email = binding.emailEt2?.text.toString()
        val password = binding.passwordEt2?.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val user = User(
                        id = currentUser?.uid ?: "",
                        name = name,
                        email = email
                    )

                    firebaseManager.saveNewUser(user)

                    Toast.makeText(context,"User is created!", Toast.LENGTH_SHORT).show()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.authFrame, SignInFragment())
                        .commit()

                } else {
                    Toast.makeText(context,"User not created", Toast.LENGTH_SHORT).show()
                    Log.d("!!!", "user not created ${task.exception}")
                }
            }
    }

}