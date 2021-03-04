package com.example.boxchat.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.adapter.ChatAdapter
import com.example.boxchat.adapter.UserAdapter
import com.example.boxchat.model.Chat
import com.example.boxchat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {
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

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

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
                Log.d("uid",userId)
                Log.d("uid",firebaseUser!!.uid)
            }
        }

        readMessage(firebaseUser!!.uid, userId)


        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                mNameFriend.text = user!!.userName

                if (user.userProfileImage == "") {
                    mAvatarChat.setImageResource(R.mipmap.ic_avatar)
                    Log.d("Test",user.userProfileImage)
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
        val databaseReferenceUser: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")


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
                        mLinearLayoutManager.scrollToPosition(chatList.size -1)
                        Log.d("Chat List Size",chatList.size.toString())
                    }
                }
                mChatRecycleView.adapter = ChatAdapter(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}