package com.example.boxchat.ui.main.admin

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.model.Chat
import com.example.boxchat.model.User
import com.example.boxchat.utils.CheckNetwork
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class AdminViewModel : ViewModel() {

    var databaseReferenceProfile = getUserId()
    var chatRef = getChatId()
    val adminRef = getAdminList()
    val me = MutableLiveData<List<User>>()
    private var yourSelfList = mutableListOf<User>()

    //list
    private var listUser = mutableListOf<User>()
    private var listChat = mutableListOf<Chat>()
    private val adminList = mutableListOf<User>()


    //list live data
    val userList = MutableLiveData<List<User>>()
    val chatList = MutableLiveData<List<Chat>>()
    val admins = MutableLiveData<List<User>>()


    init {
        getProfile()
        getNumberUser()
        getNumberChat()
        getListAdmin()
    }

    private fun getUserId(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users")
    }

    private fun getChatId(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Chat")
    }

    private fun getAdminList(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Admin")
    }

    fun addMe(yourSelf: List<User>) {
        me.value = yourSelf
    }

    fun addUser(user: List<User>) {
        userList.value = user
    }

    fun addChat(chat: List<Chat>) {
        chatList.value = chat
    }

    fun addListAdmin(admin: List<User>) {
        admins.value = admin
    }

    private fun getProfile() {
        databaseReferenceProfile.child(Firebase.auth.uid!!).addValueEventListener(object :
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

    private fun getNumberUser() {
        databaseReferenceProfile.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listUser.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    listUser.add(user!!)
                }
                addUser(listUser)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(CheckNetwork.context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getNumberChat() {
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listChat.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    listChat.add(chat!!)
                }
                addChat(listChat)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun getListAdmin() {
        adminRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adminList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val admin = dataSnapshot.getValue(User::class.java)
                    adminList.add(admin!!)
                }
                addListAdmin(adminList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}