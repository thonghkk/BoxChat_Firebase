package com.example.boxchat.ui.main.chat

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.firebaseDatabase
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.model.Chat
import com.example.boxchat.model.User
import com.example.boxchat.utils.CheckNetwork
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ChatViewModel:ViewModel() {
    val refChat = getChatReference()
    val friend = MutableLiveData<List<User>>()
    private var friendList = mutableListOf<User>()
    val friendRef = getFriendReference()
    var databaseReferenceProfile = getUserId()
    val me = MutableLiveData<List<User>>()
    private var yourSelfList = mutableListOf<User>()


    init {
        getFriendList()
        getProfile()
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

    fun addMe(yourSelf: List<User>) {
        me.value = yourSelf
    }
    private fun getProfile() {
        databaseReferenceProfile.child(auth.uid!!).addValueEventListener(object :
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


}