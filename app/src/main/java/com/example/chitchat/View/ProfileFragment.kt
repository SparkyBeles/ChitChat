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
import com.example.chitchat.Model.FirebaseManager
import com.example.chitchat.Model.User
import com.example.chitchat.R
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    var auth: FirebaseAuth = Firebase.auth

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    val vm = ChatViewModel()
    lateinit var userId: String
    var firebaseManager = FirebaseManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = auth.currentUser?.uid ?: return

        loadUser()


        binding.signOut.setOnClickListener {
            signOut()
        }
        binding.btnSaveChanges?.setOnClickListener {
            saveUser()
        }


        binding.deleteButton?.setOnClickListener {
            deleteAccount()
        }

        binding.deleteButton2?.setOnClickListener {
            deleteAccount()
        }

    }


        binding.signOut.setOnClickListener {
            signOut()
        }
        binding.btnSaveChanges?.setOnClickListener {


    private fun loadUser(){
        firebaseManager.getCurrentUser(userId) { user ->
            user?.let {
                binding.etName?.setText(it.name)
            } ?: run {
                Toast.makeText(requireContext(), "Failed to find user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUser(){
        val newName = binding.etName?.text.toString().trim()

        if (newName.isEmpty()){
            Toast.makeText(requireContext(), "Name can't be empty", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseManager.updateUser(userId, newName) { success ->
            if (success){
                Toast.makeText(requireContext(), "Update successfull!", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(requireContext(), "Update failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun signOut() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        if (auth.currentUser == null) {
            activity?.finish()
            Toast.makeText(activity, "You're signed out", Toast.LENGTH_SHORT).show()

        }
    }


    fun deleteAccount(){
        val user = FirebaseAuth.getInstance().currentUser

        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(context,"Account Deleted!",Toast.LENGTH_SHORT).show()
                signOut()

            }

        }


    }


}
