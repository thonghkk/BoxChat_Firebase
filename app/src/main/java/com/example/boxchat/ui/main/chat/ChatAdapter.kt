package com.example.boxchat.ui.main.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.model.Chat
import com.example.boxchat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatAdapter(private val chat: List<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage: TextView = itemView.findViewById(R.id.mTxtMessage)
        private val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)

        fun bindUser(chat: Chat) {
            txtMessage.text = chat.message
            Firebase.firebaseDatabase.getReference("Users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapShot: DataSnapshot in snapshot.children) {
                            val users = dataSnapShot.getValue(User::class.java)
                            if (users?.userId == chat.senderId) {
                                if (users.userProfileImage == "") {
                                    imgAvatar.setImageResource(R.mipmap.ic_avatar)
                                } else {
                                    Glide.with(itemView.context.applicationContext)
                                        .load(users.userProfileImage)
                                        .fitCenter()
                                        .circleCrop()
                                        .into(imgAvatar)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
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