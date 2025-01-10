package com.example.chitchat.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chitchat.Model.FirebaseManager
import com.example.chitchat.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    private var auth: FirebaseAuth = Firebase.auth

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userId: String
    private var firebaseManager = FirebaseManager()



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

        loadUser(userId)


        binding.btnSignOut.setOnClickListener {
            signOut()
        }

        binding.btnSaveChanges.setOnClickListener {
            saveUser()
        }

        binding.btnDelete?.setOnClickListener {
            showPopup()
        }

        binding.btnDelete2?.setOnClickListener {
            showPopup()
        }
    }


    private fun loadUser(userId: String){
        //Call the function from firebasemanager to get the user data
        firebaseManager.getCurrentUser(userId) { user ->
            //if user object is not null, update the username in the EditText
            user?.let {
                binding.etName.setText(it.name)
            } ?: run {
                //if no user was found show a toast message
                Toast.makeText(requireContext(), "Failed to find user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUser(){
        //Save the name from the edittext to a newName variable
        //Trim the whitespaces in the beginning and end
        val newName = binding.etName.text.toString().trim()

        //check if the Edittext is empty and if it is show toast and return out of the function
        if (newName.isEmpty()){
            Toast.makeText(requireContext(), "Name can't be empty", Toast.LENGTH_SHORT).show()
            return
        }
        //call for function in firebasemanager to update name of the user
        firebaseManager.updateUser(userId, newName) { success ->
            //Show toasts if successfull or failed
            if (success){
                Toast.makeText(requireContext(), "Update successfull!", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(requireContext(), "Update failed!", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun signOut() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        if (auth.currentUser == null) {
            activity?.finish()
            Toast.makeText(activity, "You're signed out", Toast.LENGTH_SHORT).show()

        }
    }


    private fun deleteAccount(){
        val user = FirebaseAuth.getInstance().currentUser

        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(context,"Account Deleted!",Toast.LENGTH_SHORT).show()
                signOut()
            }
        }
    }


    //  Popup before deleting an account.
    private fun showPopup(){
        val builder = context?.let { AlertDialog.Builder(it) }
        builder!!.setTitle("ChitChat")
            .setMessage("Do you want to delete account?")
            .setPositiveButton("Yes"){dialog,which ->
                deleteAccount()
            }
            .setNegativeButton("No") {dialog, which -> dialog.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }



}
