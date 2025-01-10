package com.example.chitchat.View

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.Model.Message
import com.example.chitchat.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
        val nameCurrentUser = itemView.findViewById<TextView>(R.id.tv_name_current_user)
        val timestampCurrentUser = itemView.findViewById<TextView>(R.id.tv_timestamp_current_user)
    }
    // ViewHolder for other user. Inherits from ViewHolder.
    inner class ViewHolderOtherUser(itemView: View) : ChatAdapter.ViewHolder(itemView) {
        val messageOtherUser = itemView.findViewById<TextView>(R.id.tv_message_other_user)
        val nameOtherUser = itemView.findViewById<TextView>(R.id.tv_name_other_user)
        val timestampOtherUser = itemView.findViewById<TextView>(R.id.tv_timestamp_other_user)
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

        // Create instance to use calendar functions, gets the current date and time.
        val messageTime = Calendar.getInstance()
        // Set the Calender instance to the message timestamp variable. timeInMillis is attribute of Calendar-class to represent when message was sent.
        // Sets the Calendar object's time to the message's timestamp received from Firebase.
        messageTime.timeInMillis = message.timestamp
        // Create instance to use calendar functions for today and yesterday. Gets the current date and time.
        val now = Calendar.getInstance()
        // Set time format based on phone locale. Used for today and yesterday.
        val timeFormat = SimpleDateFormat ("HH:mm", Locale.getDefault())
        // Set date and time for messages older than yesterday.
        val fullFormat = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault())

        // Return different formats based on today, yesterday and older.
        val formattedTime = when {
            // Return only time for messages sent today.
            // If phone's date matches with date of message, then this is displayed.
            now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR) -> {
                timeFormat.format(messageTime.time)
            }
            // Return "Yesteday, " + time for messages sent yesterday.
            // If phone's year matches with date of message, and the difference between the days is 1, then this is displayed.
            now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) -1 == messageTime.get(Calendar.DAY_OF_YEAR) -> {
                "Yesterday, " + timeFormat.format(messageTime.time)
            }
            // Return for messages older than yesterday.
            else -> {
                fullFormat.format(messageTime.time)
            }
        }

        //------------------------------------------------------------------------------------------

        // Determines which list item to bind based on the viewType set in getItemViewType.
        if(holder is ViewHolderCurrentUser) {
            holder.messageCurrentUser.text = message.message
            holder.nameCurrentUser.text = message.senderName
            holder.timestampCurrentUser.text = formattedTime
        } else if (holder is ViewHolderOtherUser) {
            holder.messageOtherUser.text = message.message
            holder.nameOtherUser.text = message.senderName
            holder.timestampOtherUser.text = formattedTime
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