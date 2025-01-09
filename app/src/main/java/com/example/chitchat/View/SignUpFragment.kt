package com.example.chitchat.View

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

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

   val party =  Party(
    speed = 0f,
    maxSpeed = 30f,
    damping = 0.9f,
    spread = 360,
    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
    position = Position.Relative(0.5, 0.3)
    )


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

                    binding.confetti?.start(party)

                    Toast.makeText(context,"User is created!", Toast.LENGTH_SHORT).show()



                    Handler(Looper.getMainLooper()).postDelayed({
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.authFrame, SignInFragment())
                            .commit()
                    }, 2000)



                } else {
                    Toast.makeText(context,"User not created", Toast.LENGTH_SHORT).show()
                    Log.d("!!!", "user not created ${task.exception}")
                }
            }
    }

}