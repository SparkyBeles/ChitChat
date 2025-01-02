package com.example.chitchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chitchat.databinding.FragmentChatBinding
import com.google.firebase.Firebase


class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? =
        null//////////////////////////min kod/////////////////////////////////
    private val binding get() = _binding!! //////////////////////////min kod/////////////////////////////////

    // myRef.setValue("Hello, World!")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(
            inflater,
            container,
            false
        )//min kod
        // Inflate the layout for this fragment

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//val whoSend = database.getReference("name")
        val bSend = binding.bSend
        val tMessage = binding.tMessage


        bSend.setOnClickListener {


        }


    }
}