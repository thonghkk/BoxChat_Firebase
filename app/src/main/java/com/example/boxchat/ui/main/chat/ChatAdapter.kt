package com.example.boxchat.ui.main.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.model.Chat
import com.example.boxchat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChatAdapter(private val chat: List<Chat>,private val user:List<User>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    private var firebaseUser: FirebaseUser? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessageRight: TextView = itemView.findViewById(R.id.mMessageRight)
        private val mAvatarMessage: ImageView = itemView.findViewById(R.id.mAvatarMessage)

        fun bindUser(chat: Chat,user:User) {
            //  val url_image = user.userProfileImage
            txtMessageRight.text = chat.message
            val url = user.userProfileImage
            Glide.with(itemView)
                .load(url)
                .override(10, 10)
                .fitCenter()
                .into(mAvatarMessage)
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
        holder.bindUser(chat[position],user[position])
    }

    override fun getItemCount() = chat.size

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (chat[position].senderId == firebaseUser!!.uid) {
            Log.d("checka", chat[position].senderId)
            Log.d("checka", firebaseUser!!.uid)
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }
    }
}