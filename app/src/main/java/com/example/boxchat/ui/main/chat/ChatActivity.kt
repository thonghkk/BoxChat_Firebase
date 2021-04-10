package com.example.boxchat.ui.main.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Constants.Companion.CHANNEL_CHAT
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.model.Chat
import com.example.boxchat.model.Notification
import com.example.boxchat.model.PushNotification
import com.example.boxchat.model.User
import com.example.boxchat.network.FirebaseService
import com.example.boxchat.network.RetrofitInstance
import com.example.boxchat.ui.main.MainActivity
import com.example.boxchat.ui.main.stranger.ViewStrangerActivity
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ChatActivity : BaseActivity() {
    private lateinit var mNameFriend: TextView
    private lateinit var mBtnBackMessageFriend: ImageView
    private lateinit var mBtnSendMessage: ImageButton
    private lateinit var mAvatarChat: CircleImageView
    private lateinit var mEnterMessage: EditText
    private lateinit var mChatRecycleView: RecyclerView
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var mChatViewModel: ChatViewModel
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
        mChatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        mLinearLayoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        mChatRecycleView.layoutManager = mLinearLayoutManager
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/${user?.uid}")
        //get value SharedPreferences
        FirebaseService.sharePref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            FirebaseService.token = it.result.token
            Log.d("token", it.result.token)
        }
        val userId = intent.getStringExtra("userId")

        mBtnBackMessageFriend.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        mAvatarChat.setOnClickListener {
            val intent = Intent(this, ViewStrangerActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        mChatViewModel.friend.observe(this, Observer { friend ->
            for (j in friend) {
                if (j.userId == userId) {
                    mNameFriend.text = j.userName
                    if (j.userProfileImage == "") {
                        mAvatarChat.setImageResource(R.mipmap.ic_avatar)
                    } else {
                        Glide.with(this@ChatActivity)
                            .load(j.userProfileImage)
                            .fitCenter()
                            .into(mAvatarChat)
                    }
                    mChatViewModel.me.observe(this, Observer { me ->
                        for (i in me) {
                            sendMessage(j.userId, i.userName)
                        }
                    })
                }
            }
        })
        readMessage(auth.uid!!, userId!!)
    }

    private fun sendMessage(userId: String, userName: String) {
        mBtnSendMessage.setOnClickListener {
            val message: String = mEnterMessage.text.toString()
            if (message.isBlank()) {
                Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show()
                mEnterMessage.setText("")
            } else {
                pushMessage(auth.uid!!, userId, message)
                mEnterMessage.setText("")
                topic = "/topics/$userId"
                PushNotification(
                    Notification(userName, message, auth.uid!!, CHANNEL_CHAT),
                    topic
                ).also {
                    sendNotification(it)
                }
            }
        }
    }

    private fun pushMessage(senderId: String, receiverId: String, message: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        mChatViewModel.refChat.push().setValue(hashMap)
    }

    private fun readMessage(senderId: String, receiverId: String) {
        mChatViewModel.refChat.addValueEventListener(object : ValueEventListener {
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

            }
        })
    }

    //sent message and show notification on Receiver
    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
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
//                Toast.makeText(this@ChatActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
