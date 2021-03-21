package com.example.boxchat.ui.main.users

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.chat.ChatActivity

class UserAdapter(private val user: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtUserName: TextView = itemView.findViewById(R.id.mNameUser)
        private val imgAvatar: ImageView = itemView.findViewById(R.id.mAvatar)
        private val mLayoutUser: LinearLayout = itemView.findViewById(R.id.mLayoutUser)
        fun bindUser(user: User) {
            txtUserName.text = user.userName
            val url = user.userProfileImage
            Glide.with(itemView)
                .load(url)
                .placeholder(R.mipmap.ic_avatar)
                .fitCenter()
                .into(imgAvatar)
            Log.d("test", url)
            mLayoutUser.setOnClickListener {
                val intent = Intent(itemView.context, ViewStrangerActivity::class.java)
                intent.putExtra("userId", user.userId)
                intent.putExtra("userName", user.userName)
                intent.putExtra("userImage", user.userProfileImage)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.items_user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindUser(user[position])
    }

    override fun getItemCount() = user.size

}