package com.example.boxchat.ui.main.users

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.ui.main.chat.ChatActivity

class UserLocalAdapter(private val userLocal: List<UserLocal>) :
    RecyclerView.Adapter<UserLocalAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txtUserName: TextView = itemView.findViewById(R.id.mNameUser)
        private val imgAvatar: ImageView = itemView.findViewById(R.id.mAvatar)
        private val mLayoutUser: LinearLayout = itemView.findViewById(R.id.mLayoutUser)

        fun bindUser(userLocal: UserLocal) {
            txtUserName.text = userLocal.userName
            val url = userLocal.userProfileImage
            Glide.with(itemView)
                .load(url)
                .placeholder(R.mipmap.ic_avatar)
                .fitCenter()
                .into(imgAvatar)
            Log.d("test2", url)

            mLayoutUser.setOnClickListener {
                val intent = Intent(itemView.context, ViewStrangerActivity::class.java)
                intent.putExtra("userId", userLocal.userId)
                intent.putExtra("userName", userLocal.userName)
                itemView.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.items_user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindUser(userLocal[position])
    }

    override fun getItemCount() = userLocal.size


}