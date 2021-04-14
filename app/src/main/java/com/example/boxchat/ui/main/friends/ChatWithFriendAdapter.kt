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
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.model.Chat
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.chat.ChatActivity
import com.example.boxchat.ui.main.chat.ChatAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class ChatWithFriendAdapter(private var user: List<User>) :
    RecyclerView.Adapter<ChatWithFriendAdapter.ViewHolder>(), Filterable {
    private val userOld: List<User> = user

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatList = mutableListOf<Chat>()
        private val userList = mutableListOf<User>()
        private val txtUserName: TextView = itemView.findViewById(R.id.mNameFriends)
        private val imgAvatar: ImageView = itemView.findViewById(R.id.mAvatarFriend)
        private val mMessageLast: TextView = itemView.findViewById(R.id.mMessageLast)
        private val mLayoutUser: LinearLayout = itemView.findViewById(R.id.mLayoutFriend)
        private var mImgStatusOn: ImageView = itemView.findViewById(R.id.mImgStatusOn)
        private var mImgStatusOff: ImageView = itemView.findViewById(R.id.mImgStatusOff)

        fun bindUser(friend: User) {
            Log.d("MEO", friend.userName)
            Log.d("MEO", friend.userId)
            lastMessage(friend.userId, mMessageLast)
            Firebase.firebaseDatabase.getReference("Users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            val mUsers = dataSnapshot.getValue(User::class.java)
                            if (mUsers?.userId == friend.userId) {
                                txtUserName.text = mUsers.userName
                                val url = mUsers.userProfileImage
                                Glide.with(itemView.context.applicationContext)
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

            mLayoutUser.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("userId", friend.userId)
                itemView.context.startActivity(intent)
            }
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
                                mImgStatusOn.visibility = View.VISIBLE
                                mImgStatusOff.visibility = View.GONE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        // get last message for user
        private fun lastMessage(userId: String, message: TextView) {
            Firebase.firebaseDatabase.getReference("Chat").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val chat = dataSnapShot.getValue(Chat::class.java)
                        if ((chat!!.senderId == userId && chat.receiverId == auth.uid!!) ||
                            (chat.senderId == auth.uid!! && chat.receiverId == userId)
                        ) {
                            chat.message
                            chatList.add(chat)
                            message.text = chatList.last().message
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
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
                } else {
                    val list = mutableListOf<User>()
                    for (i in userOld) {
                        if (i.userName.toLowerCase().contains(strSearch.toLowerCase())) {
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