package com.example.chitchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchat.databinding.FragmentFriendsBinding


class FriendsFragment : Fragment() {


    private var _binding: FragmentFriendsBinding? =
        null//////////////////////////min kod/////////////////////////////////
    private val binding get() = _binding!! //////////////////////////min kod/////////////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFriendsBinding.inflate(
            inflater,
            container,
            false
        )//min kod
        // Inflate the layout for this fragment


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var friendList = mutableListOf<String>()
       friendList.add("David")
        friendList.add("Per")
        friendList.add("Susann")

        val friendsRecycler = binding.friendsRecycler
        friendsRecycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = FriendsAdapter(requireContext(), friendList)
        friendsRecycler.adapter = adapter
    }

}