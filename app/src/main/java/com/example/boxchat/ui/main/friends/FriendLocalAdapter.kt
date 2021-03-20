package com.example.boxchat.ui.main.friends

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.databaselocal.entity.FriendLocal
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.chat.ChatActivity

class FriendLocalAdapter(private val user: List<FriendLocal>) : RecyclerView.Adapter<FriendLocalAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txtUserName: TextView = itemView.findViewById(R.id.mNameFriends)
        private val imgAvatar: ImageView = itemView.findViewById(R.id.mAvatarFriend)
        private val mLayoutUser: LinearLayout = itemView.findViewById(R.id.mLayoutFriend)

        fun bindUser(friend: FriendLocal) {
            txtUserName.text = friend.userName
            Log.d("MEO",friend.userName)
            Log.d("MEO",friend.userId)
            val url = friend.userProfileImage

            Glide.with(itemView)
                .load(url)
                .placeholder(R.mipmap.ic_avatar)
                .fitCenter()
                .transform()
                .into(imgAvatar)
            Log.d("test", url)

            mLayoutUser.setOnClickListener {
                Toast.makeText(itemView.context,"Internet access for texting",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.items_friend, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindUser(user[position])
    }

    override fun getItemCount() = user.size
}