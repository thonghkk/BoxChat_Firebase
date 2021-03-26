package com.example.boxchat.ui.main.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.R
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.model.Chat

class ChatAdapter(private val chat: List<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessageRight: TextView = itemView.findViewById(R.id.mMessageRight)

        fun bindUser(chat: Chat) {
            txtMessageRight.text = chat.message

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MESSAGE_TYPE_RIGHT) {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_right, parent, false)
            return ViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_left, parent, false)
            return ViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindUser(chat[position])
    }

    override fun getItemCount() = chat.size

    override fun getItemViewType(position: Int): Int {

        if (chat[position].senderId == auth.uid) {
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }
    }
}