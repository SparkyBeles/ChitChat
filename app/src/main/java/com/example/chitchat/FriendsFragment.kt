package com.example.chitchat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchat.databinding.FragmentFriendsBinding
import com.google.firebase.auth.FirebaseAuth


class FriendsFragment : Fragment() {


    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatViewModel
    private lateinit var friendList: MutableList<User>
    private lateinit var adapter: FriendsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        friendList = mutableListOf()
        adapter = FriendsAdapter(requireContext(), friendList) { friend ->
//            openChatFragment(friend)
        }
        binding.friendsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.friendsRecycler.adapter = adapter

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            viewModel.loadFriends(currentUserId).observe(viewLifecycleOwner) { friends ->
                Log.d("FriendsFragment", "Friends loaded: ${friends.size} friends found")
                friendList.clear()
                friendList.addAll(friends)
                adapter.notifyDataSetChanged()
            }
        }

        binding.fabAddFriends.setOnClickListener {
            showAddFriend()
        }
    }

    fun showAddFriend() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_friend, null)
        val emailET = dialogView.findViewById<EditText>(R.id.etAddFriendEmail)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Friend")
            .setView(dialogView)
            .setPositiveButton("Add") { dialogInterface, _ ->
                val email = emailET.text.toString()
                if (email.isNotEmpty()) {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                    if (currentUserId != null) {
                        viewModel.addFriend(currentUserId, email)
                    }
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton("Exit") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }


//    fun openChatFragment(friend: User){
//        Log.d("FriendsFragment", "Opening chat with friend: id=${friend.id}, name=${friend.name}")
//        val chatFragment = ChatFragment.newInstance(friend.id, friend.name)
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.chatRecycler, chatFragment)
//            .addToBackStack(null)
//            .commit()
//    }

}