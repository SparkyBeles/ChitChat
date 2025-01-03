package com.example.chitchat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchat.databinding.FragmentFriendsBinding
import com.google.firebase.firestore.QuerySnapshot


class FriendsFragment : Fragment() {


    private var _binding: FragmentFriendsBinding? =
        null
    private val binding get() = _binding!!
    val vm = ChatViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFriendsBinding.inflate(
            inflater,
            container,
            false
        )


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var friendList = mutableListOf<String>()
//       friendList.add("David")
//        friendList.add("Per")
//        friendList.add("Susann")

        val friendsRecycler = binding.friendsRecycler
        friendsRecycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = FriendsAdapter(requireContext(), friendList)
        friendsRecycler.adapter = adapter

        vm.friends.observe(viewLifecycleOwner) { friends ->
            Log.d("FriendsObserver", "Observer triggered with ${friends.size} friends")
            friendList.clear()
            friends.forEach { friend ->
                friendList.add(friend.name)
            }
            adapter.notifyDataSetChanged()
        }


    }

}