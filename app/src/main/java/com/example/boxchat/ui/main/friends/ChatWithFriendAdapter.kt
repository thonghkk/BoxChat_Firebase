package com.example.boxchat.ui.main.friends

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.chat.ChatActivity

class ChatWithFriendAdapter(private var user: List<User>) : RecyclerView.Adapter<ChatWithFriendAdapter.ViewHolder>(), Filterable {
    private val userOld: List<User> = user

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtUserName: TextView = itemView.findViewById(R.id.mNameFriends)
        private val imgAvatar: ImageView = itemView.findViewById(R.id.mAvatarFriend)
        private val mLayoutUser: LinearLayout = itemView.findViewById(R.id.mLayoutFriend)

        fun bindUser(friend: User) {
            txtUserName.text = friend.userName
            Log.d("MEO", friend.userName)
            Log.d("MEO", friend.userId)
            val url = friend.userProfileImage

            Glide.with(itemView)
                .load(url)
                .placeholder(R.mipmap.ic_avatar)
                .fitCenter()
                .transform()
                .into(imgAvatar)
            Log.d("test", url)

            mLayoutUser.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("userId", friend.userId)
                intent.putExtra("userName", friend.userName)
                intent.putExtra("userImage", friend.userProfileImage)
                itemView.context.startActivity(intent)
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val strSearch = constraint.toString()
                if (strSearch.isEmpty()) {
                    user = userOld
                }else{
                    val list = mutableListOf<User>()
                    for (i in userOld){
                        if (i.userName.toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(i)
                        }
                    }
                    user = list
                }

                val filterResult = FilterResults()
                filterResult.values = user
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                user = results?.values as List<User>
                notifyDataSetChanged()
            }

        }
    }
}