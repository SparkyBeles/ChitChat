package com.example.chitchat.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? =
        null
    private val binding get() = _binding!!
    val vm = ChatViewModel()
    //  var auth: FirebaseAuth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )
binding.signOut.setOnClickListener {
    signOut()
}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    fun signOut() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        if (auth.currentUser == null) {
            activity?.finish()
             Toast.makeText(activity, "You're signed out", Toast.LENGTH_SHORT).show()

        }
    }

}
