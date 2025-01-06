package com.example.chitchat.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chitchat.Model.User
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    var auth: FirebaseAuth = Firebase.auth
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

     //   binding.tvUserName = current user name
 //   binding.etName = current user name
 //   binding.etEmail = current email



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
