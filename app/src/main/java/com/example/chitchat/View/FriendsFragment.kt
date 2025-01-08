package com.example.chitchat.View

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
import com.example.chitchat.R
import com.example.chitchat.Model.User
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.FragmentFriendsBinding
import com.google.firebase.auth.FirebaseAuth


class FriendsFragment : Fragment() {


    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatViewModel
    private lateinit var friendList: MutableList<User>
    private lateinit var adapter: FriendsAdapter
    private var receiverId : String? = null // Receiver's ID when a friend is clicked.
    private var receiverName : String? = null // Receiver's name when a friend is clicked.
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid


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
            // Callback function when a friend is clicked
            receiverId = friend.id // Set the receiverId to the clicked friend's ID
            receiverName = friend.name // Set the receiverName to the clicked friend's name

            // call openChat function in viewModel, which in turn calls createChatCollectionIfNeeded in FirebaseManager.
            // uses id from friend and currentUser to create chat collection with unique ID.
            viewModel.openChat(friend.id, currentUserId ?: "") //
        }
        binding.friendsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.friendsRecycler.adapter = adapter


        // Observe if friends for the currentUser has been updated
        if (currentUserId != null) {
            viewModel.loadFriends(currentUserId).observe(viewLifecycleOwner) { friends ->
                Log.d("FriendsFragment", "Friends loaded: ${friends.size} friends found")
                friendList.clear()
                friendList.addAll(friends)
                adapter.notifyDataSetChanged()
            }
        }


        // Observe if chatCollectionId has been updated/set in viewModel.
        viewModel.chatCollectionId.observe(viewLifecycleOwner) { chatCollectionId ->
            if (chatCollectionId != null) {
                // Chat collection ID has been set, open the ChatFragment
                // Send the two arguments for ChatFragment to open.
                openChatFragment(chatCollectionId, receiverId!!)
            }
        }

        binding.fabAddFriends?.setOnClickListener {
            showAddFriend()
        }
    }

    fun showAddFriend() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_friend, null)
        val emailET = dialogView.findViewById<EditText>(R.id.etAddFriendEmail)

        //Create the AlertDialog to show dialog window
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Friend")
            .setView(dialogView) //Set the layout for the dialog
            //Add 2 buttons for Add and Exit
            .setPositiveButton("Add") { dialogInterface, _ ->
                val email = emailET.text.toString()
                //If edittext is not empty get current users ID
                if (email.isNotEmpty()) {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                    //If user is logged in and ID is found call the function in the viewmodel
                    if (currentUserId != null) {
                        viewModel.addFriend(currentUserId, email)
                    }
                }
                dialogInterface.dismiss() //close dialog
            }
            .setNegativeButton("Exit") { dialogInterface, _ ->
                dialogInterface.dismiss() //close dialog
            }
            .create()

        dialog.show()
    }


    // Creates a ChatFragment that takes in two arguments.
    private fun openChatFragment(chatCollectionId:String, receiverId:String) {
        val chatFragment = ChatFragment.newInstance(chatCollectionId, receiverId, receiverName!!)
        parentFragmentManager.beginTransaction().apply{
            replace(R.id.friendsOrChat, chatFragment)
            addToBackStack(null)
            commit()
        }
    }


}