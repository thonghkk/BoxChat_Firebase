package com.example.boxchat.ui.main.friends

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.commom.Firebase
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.chat.ChatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatWithFriendAdapterCircle(private var friends: List<User>) :
    RecyclerView.Adapter<ChatWithFriendAdapterCircle.ViewHolder>(), Filterable {
    private val userOld: List<User> = friends

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgAvatar: ImageView = itemView.findViewById(R.id.mImgIcon)
        private val mNameFriend: TextView = itemView.findViewById(R.id.mNameFriend)
        private val mLineaLayout: LinearLayout = itemView.findViewById(R.id.mLineaLayout)
        private val mImgStatusOnCircle: ImageView = itemView.findViewById(R.id.mImgStatusOnCircle)
        private val userList = mutableListOf<User>()

        fun bindItems(friend: User) {
            // val url = friend.userProfileImage
            //get info user
            Firebase.firebaseDatabase.getReference("Users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            val mUsers = dataSnapshot.getValue(User::class.java)
                            if (mUsers?.userId == friend.userId) {

                                mNameFriend.text = mUsers.userName
                                val url = mUsers.userProfileImage
                                Glide.with(itemView)
                                    .load(url)
                                    .placeholder(R.mipmap.ic_avatar)
                                    .fitCenter()
                                    .transform()
                                    .into(imgAvatar)
                                Log.d("test", url)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            //custom click user
            mLineaLayout.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("userId", friend.userId)
                itemView.context.startActivity(intent)
            }

            //get status of user
            Firebase.firebaseDatabase.getReference("Users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapShot: DataSnapshot in snapshot.children) {
                            val users = dataSnapShot.getValue(User::class.java)
                            if (users?.userId == friend.userId) {
                                userList.add(users)
                            }
                        }
                        for (i in userList) {
                            if (i.statusExist == "online") {
                                mImgStatusOnCircle.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.items_friend_circle, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(friends[position])
    }

    override fun getItemCount() = friends.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val strSearch = constraint.toString()
                if (strSearch.isEmpty()) {
                    friends = userOld
                } else {
                    val list = mutableListOf<User>()
                    for (i in userOld) {
                        if (i.userName.toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(i)
                        }
                    }
                    friends = list
                }

                val filterResult = FilterResults()
                filterResult.values = friends
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                friends = results?.values as List<User>
                notifyDataSetChanged()
            }
        }
    }
}