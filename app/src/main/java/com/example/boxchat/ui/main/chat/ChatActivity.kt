package com.example.boxchat.ui.main.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.model.Chat
import com.example.boxchat.model.Notification
import com.example.boxchat.model.PushNotification
import com.example.boxchat.model.User
import com.example.boxchat.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.lang.Exception

class ChatActivity : BaseActivity() {
    private var firebaseUser: FirebaseUser? = null
    private var reference: DatabaseReference? = null
    private lateinit var mNameFriend: TextView
    private lateinit var mBtnBackMessageFriend: ImageView
    private lateinit var mBtnSendMessage: ImageButton
    private lateinit var mAvatarChat: CircleImageView
    private lateinit var mEnterMessage: EditText
    private lateinit var mChatRecycleView: RecyclerView
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private val chatList = mutableListOf<Chat>()
    private val userList = mutableListOf<User>()
    var topic = ""

    override fun getLayoutID() = R.layout.activity_chat

    @SuppressLint("WrongConstant")
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mAvatarChat = findViewById(R.id.mAvatarChat)
        mNameFriend = findViewById(R.id.mNameFriend)
        mBtnBackMessageFriend = findViewById(R.id.mBtnBackMessageFriend)
        mBtnSendMessage = findViewById(R.id.mSendMessage)
        mEnterMessage = findViewById(R.id.mEnterMessage)
        mChatRecycleView = findViewById(R.id.mRecycleChat)

        mLinearLayoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        mChatRecycleView.layoutManager = mLinearLayoutManager

        mBtnBackMessageFriend.setOnClickListener {
            onBackPressed()
        }

        val userId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("userName")

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        mBtnSendMessage.setOnClickListener {
            val message: String = mEnterMessage.text.toString()
            if (message.isBlank()) {
                Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show()
                mEnterMessage.setText("")
            } else {
                sendMessage(firebaseUser!!.uid, userId, message)
                mEnterMessage.setText("")
                Log.d("uid", userId)
                Log.d("uid", firebaseUser!!.uid)

                /*Error : Fixing*/
//                topic = "/topics/$userId"
//                PushNotification(Notification(userName!!, message), topic).also {
//                    sendNotification(it)
//                }
            }
        }
        readMessage(firebaseUser!!.uid, userId)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                mNameFriend.text = user!!.userName

                if (user.userProfileImage == "") {
                    mAvatarChat.setImageResource(R.mipmap.ic_avatar)
                    Log.d("Test", user.userProfileImage)
                } else {
                    Glide.with(this@ChatActivity)
                        .load(user.userProfileImage)
                        .fitCenter()
                        .into(mAvatarChat)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference

        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        reference.child("Chat").push().setValue(hashMap)
    }

    private fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if ((chat!!.senderId == senderId && chat.receiverId == receiverId) ||
                        (chat.senderId == receiverId && chat.receiverId == senderId)
                    ) {
                        chatList.add(chat)
                        //Scroll to bottom message
                        mLinearLayoutManager.scrollToPosition(chatList.size - 1)
                        Log.d("Chat List Size", chatList.size.toString())
                    }
                }
                mChatRecycleView.adapter = ChatAdapter(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //sent message and show notification on Receiver
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.notificationApi.postNotification(notification)
            if (response.isSuccessful) {
                Toast.makeText(
                    this@ChatActivity,
                    " Response : ${Gson().toJson(response)}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@ChatActivity,
                    response.errorBody().toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@ChatActivity, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}