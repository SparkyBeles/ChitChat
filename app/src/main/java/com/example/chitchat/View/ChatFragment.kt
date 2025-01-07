package com.example.chitchat.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchat.Model.Message
import com.example.chitchat.Model.User
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.FragmentChatBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

// Stores information from FriendsFragment sent when ChatFragment created.
private const val ARG_CHAT_COLLECTION_ID = "chatCollectionId"
private const val ARG_RECEIVER_ID = "receiverId"
private const val ARG_RECEIVER_NAME = "receiverName"


class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? =
        null
    private val binding get() = _binding!!
    lateinit var db : FirebaseFirestore
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var currentUserName : String? = null
    lateinit var vm : ChatViewModel
    private var param1: String? = null
    private var param2: String? = null
    private var param3: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_CHAT_COLLECTION_ID)
            param2 = it.getString(ARG_RECEIVER_ID)
            param3 = it.getString(ARG_RECEIVER_NAME)
        }
    }


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

        vm = ViewModelProvider(this)[ChatViewModel::class.java]

        db = Firebase.firestore
        val messages = mutableListOf<Message>()


        val chatRecycler = binding.chatRecycler
        chatRecycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ChatAdapter(requireContext(), messages, currentUserId)
        chatRecycler.adapter = adapter

        // Get chat collection ID from arguments, sent from FriendsFragment.
        val chatCollectionId = arguments?.getString(ARG_CHAT_COLLECTION_ID)

        if (chatCollectionId != null) {
            vm.getAllMessages(chatCollectionId)
            vm.messages.observe(viewLifecycleOwner) { newMessages ->
                messages.clear()
                messages.addAll(newMessages)
                adapter.notifyDataSetChanged()
            }
        } else {
            // Add code if chatCollectionId is null.
        }


        if (currentUserId != null) {
            vm.fetchCurrentUser(currentUserId)
            vm.currentUser.observe(viewLifecycleOwner) { currentUser ->
                if (currentUser != null) {
                    currentUserName = currentUser.name
                    Log.d("!!!", "current user: ${currentUser?.name}")
                } else {
                    Log.d("!!!", "current user is null")

                }
            }
        }

        binding.bSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString()
            val receiverId = arguments?.getString("receiverId") // Get receiverId from arguments
            val receiverName = arguments?.getString("receiverName") // Get receiverName from arguments

            if (messageText.isNotEmpty() && chatCollectionId != null && currentUserId != null && receiverId != null) {
                val message = Message(
                    id = "",
                    senderId = currentUserId,
                    senderName = currentUserName!!,
                    receiverId = receiverId,
                    receiverName = receiverName!!,
                    message = messageText,
                    timestamp = System.currentTimeMillis()
                )

                // Use viewModel reference to send message to Firebase.
                // success in callback is a boolean, if true, message is sent.
                // sendMessage is a function in FirebaseManager.
                vm.sendMessage(chatCollectionId, message) { success ->
                    if (success) {
                        binding.etMessage.text.clear()
                    } else {
                        Log.e("!!!", "Failed to send message")
                    }
                }
            }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        // Renamed arguments from FriendsFragment.
        fun newInstance(param1: String, param2: String, param3: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHAT_COLLECTION_ID, param1)
                    putString(ARG_RECEIVER_ID, param2)
                    putString(ARG_RECEIVER_NAME, param3)
                }
            }
    }

}