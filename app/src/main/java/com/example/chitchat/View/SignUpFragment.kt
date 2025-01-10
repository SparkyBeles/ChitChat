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
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class SignUpFragment : Fragment() {

    //  Firebase Authentication instance to handle user authentication processes.
    private var auth: FirebaseAuth = Firebase.auth

   //  Instance of FirebaseManager.
    private val firebaseManager = FirebaseManager()

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )
        val signUpBtn = binding.btnSignup
        signUpBtn.setOnClickListener {
            signUp()


        }
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    //  Confetti preset.
    private fun parade(): List<Party> {
        val party = Party(
            speed = 10f,
            maxSpeed = 30f,
            damping = 0.9f,
            angle = Angle.RIGHT - 45,
            spread = Spread.SMALL,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(30),
            position = Position.Relative(0.0, 0.5))

        return listOf(
            party,
            party.copy(
                angle = party.angle - 90, // flip angle from right to left
                position = Position.Relative(1.0, 0.5)),
            )
    }



    //  Sign-up function with a confetti preset triggered upon successful sign-up.
    private fun signUp() {
        val name = binding.etUserName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

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

                    binding.confetti.start(parade())

                    Toast.makeText(context,"User is created!", Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fcv_auth, SignInFragment())
                            .commit()
                    }, 3000)

                } else {
                    Toast.makeText(context,"User not created", Toast.LENGTH_SHORT).show()
                    Log.d("!!!", "user not created ${task.exception}")
                }
            }
    }

}