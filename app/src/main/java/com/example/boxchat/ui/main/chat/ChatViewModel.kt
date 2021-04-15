package com.example.boxchat.ui.main.chat

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.firebaseDatabase
import com.example.boxchat.model.Chat
import com.example.boxchat.model.User
import com.example.boxchat.utils.CheckNetwork
import com.google.firebase.database.*

class ChatViewModel : ViewModel() {
    private var friendList = mutableListOf<User>()
    private val friendRef = getFriendReference()
    private var yourSelfList = mutableListOf<User>()
    private var userList = mutableListOf<User>()
    private var chatList = mutableListOf<Chat>()
    val refChat = getChatReference()
    val friend = MutableLiveData<List<User>>()
    var userRef = getUserId()
    val me = MutableLiveData<List<User>>()
    val mUsers = MutableLiveData<List<User>>()
    val mChat = MutableLiveData<List<Chat>>()

    init {
        getFriendList()
        getProfile()
        getUser()
        getChat()
    }

    private fun getChatReference(): DatabaseReference {
        return firebaseDatabase.getReference("Chat")
    }

    private fun getFriendReference(): DatabaseReference {
        return firebaseDatabase.getReference("Friends")
    }

    private fun getUserId(): DatabaseReference {
        return firebaseDatabase.getReference("Users")
    }

    fun addListFriend(user: List<User>) {
        friend.value = user
    }

    fun addMe(yourSelf: List<User>) {
        me.value = yourSelf
    }

    fun addUser(user: List<User>) {
        mUsers.value = user
    }

    fun addChat(chat: List<Chat>) {
        mChat.value = chat
    }

    private fun getFriendList() {
        friendRef.child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendList.clear()
                    Log.d("snapshot", "User: $snapshot  ")
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val mReceive = dataSnapShot.getValue(User::class.java)
                        Log.d("ttt22", "$dataSnapShot")
                        friendList.add(mReceive!!)
                        addListFriend(friendList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(CheckNetwork.context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getProfile() {
        userRef.child(auth.uid!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                yourSelfList.add(user!!)
                addMe(yourSelfList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(CheckNetwork.context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUser() {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    userList.add(user!!)
                    addUser(userList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getChat(){
        refChat.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 for (dataSnapshot:DataSnapshot in snapshot.children){
                     val chat = dataSnapshot.getValue(Chat::class.java)
                     chatList.add(chat!!)
                 }
                addChat(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}