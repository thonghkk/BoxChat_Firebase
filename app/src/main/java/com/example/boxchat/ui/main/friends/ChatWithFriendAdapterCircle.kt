package com.example.boxchat.ui.main.friends

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.chat.ChatActivity

class ChatWithFriendAdapterCircle(private val friends:List<User>): RecyclerView.Adapter<ChatWithFriendAdapterCircle.ViewHolder>()  {

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        private val imgAvatar: ImageView = itemView.findViewById(R.id.mImgIcon)
        private val mNameFriend: TextView = itemView.findViewById(R.id.mNameFriend)
        private val mLineaLayout: LinearLayout = itemView.findViewById(R.id.mLineaLayout)

        fun bindItems(friend:User){
            mNameFriend.text = friend.userName
            val url = friend.userProfileImage
            Glide.with(itemView)
                .load(url)
                .placeholder(R.mipmap.ic_avatar)
                .fitCenter()
                .transform()
                .into(imgAvatar)

            mLineaLayout.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("userId",friend.userId)
                intent.putExtra("userName",friend.userName)
                intent.putExtra("userImage",friend.userProfileImage)
                itemView.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.items_friend_circle,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bindItems(friends[position])
    }

    override fun getItemCount() = friends.size
}