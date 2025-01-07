package com.example.chitchat.View

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.Model.Message
import com.example.chitchat.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ChatAdapter(
    val context: Context,
    val messages: MutableList<Message>,
    val currentUserId : String ?= null) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    // Constants for view types, used to determined which list item layout to display.
    private val VIEW_TYPE_CURRENT_USER = 1
    private val VIEW_TYPE_OTHER_USER = 2

    //-------------------VIEWHOLDERS----------------------------------------------------------------
    open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    // ViewHolder for current user. Inherits from ViewHolder.
    inner class ViewHolderCurrentUser(itemView: View) : ChatAdapter.ViewHolder(itemView) {
        val messageCurrentUser = itemView.findViewById<TextView>(R.id.tv_message_current_user)
        val nameCurrentUser = itemView.findViewById<TextView>(R.id.name_current_user)
    }
    // ViewHolder for other user. Inherits from ViewHolder.
    inner class ViewHolderOtherUser(itemView: View) : ChatAdapter.ViewHolder(itemView) {
        val messageOtherUser = itemView.findViewById<TextView>(R.id.tv_message_other_user)
        val nameOtherUser = itemView.findViewById<TextView>(R.id.tv_name_other_user)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Determines which list item layout to inflate based on the viewType set in getItemViewType.
        return if(viewType == VIEW_TYPE_CURRENT_USER) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.chat_item_current_user, parent, false)
            ViewHolderCurrentUser(itemView)
        } else {
            val itemView = LayoutInflater.from(context).inflate(R.layout.chat_item_other_user, parent, false)
            ViewHolderOtherUser(itemView)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
//        holder.message.text = message.message

        // Determines which list item to bind based on the viewType set in getItemViewType.
        if(holder is ViewHolderCurrentUser) {
            holder.messageCurrentUser.text = message.message
            holder.nameCurrentUser.text = message.senderName
        } else if (holder is ViewHolderOtherUser) {
            holder.messageOtherUser.text = message.message
            holder.nameOtherUser.text = message.senderName
        }
    }


    // Determines which list item layout to display based on the senderId.
    // If senderId is currentUserId, return VIEW_TYPE_CURRENT_USER, else return VIEW_TYPE_OTHER_USER.
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if(message.senderId == currentUserId) {
            VIEW_TYPE_CURRENT_USER
        }else {
            VIEW_TYPE_OTHER_USER
        }
    }


    override fun getItemCount(): Int {
        return messages.size
    }
}