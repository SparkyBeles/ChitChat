package com.example.chitchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchat.databinding.FragmentChatBinding
import com.google.firebase.Firebase


class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? =
        null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bSend = binding.bSend

        var messages = mutableListOf<String>()
        messages.add("Hello")
        messages.add("How are you?")
        bSend.setOnClickListener {


        }
        val chatRecycler = binding.chatRecycler
        chatRecycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ChatAdapter(requireContext(), messages)
        chatRecycler.adapter = adapter

    }

}