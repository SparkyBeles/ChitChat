package com.example.chitchat.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchat.Model.Message
import com.example.chitchat.databinding.FragmentChatBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

// Stores information from FriendsFragment sent when ChatFragment created.
private const val ARG_CHAT_COLLECTION_ID = "chatCollectionId"
private const val ARG_RECEIVER_ID = "receiverId"


class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? =
        null
    private val binding get() = _binding!!
    lateinit var db : FirebaseFirestore
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid


    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_CHAT_COLLECTION_ID)
            param2 = it.getString(ARG_RECEIVER_ID)
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


        db = Firebase.firestore
        val messages = mutableListOf<Message>()


        val chatRecycler = binding.chatRecycler
        chatRecycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ChatAdapter(requireContext(), messages)
        chatRecycler.adapter = adapter

        // Get chat collection ID from arguments, sent from FriendsFragment.
        val chatCollectionId = arguments?.getString(ARG_CHAT_COLLECTION_ID)
        val messagesCollectionRef = db // Define messagesCollectionRef
            .collection("chats")
            .document(chatCollectionId ?: "")
            .collection("messages")


        if(chatCollectionId != null) {
            // Fetch and update chat history if the collection Id exists. Order by timestamp.
            messagesCollectionRef.orderBy("timestamp").addSnapshotListener { snapshot, exception ->
                if(exception != null) {
                    return@addSnapshotListener
                }

                // Fetch new messages and add them to the list.
                val newMessages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                messages.clear()
                messages.addAll(newMessages)
                adapter.notifyDataSetChanged()
            }

        } else {
            // Add code if chatCollectionId is null.
        }


        // Send message when button is clicked.
        binding.bSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString()
            val receiverId = arguments?.getString("receiverId") // Get receiverId from arguments

            if(messageText.isNotEmpty() && chatCollectionId != null && currentUserId != null && receiverId != null) {
                // Create a message object with the relevant information.
                val message = Message(
                    id = "",
                    senderId = currentUserId,
                    receiverId = receiverId,
                    message = messageText,
                    timestamp = System.currentTimeMillis()
                )

                // Add message to Firestore collection.
                messagesCollectionRef.add(message)
                    .addOnSuccessListener { documentReference ->
                        binding.etMessage.text.clear()
                    }
                    .addOnFailureListener { e ->
                        // Add code if message sending fails.
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
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHAT_COLLECTION_ID, param1)
                    putString(ARG_RECEIVER_ID, param2)
                }
            }
    }

}