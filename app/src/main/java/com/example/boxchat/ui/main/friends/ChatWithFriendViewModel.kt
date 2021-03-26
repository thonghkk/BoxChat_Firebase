package com.example.boxchat.ui.main.friends

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.boxchat.utils.CheckNetwork.Companion.context
import com.example.boxchat.commom.Firebase
import com.example.boxchat.databaselocal.FriendLocalDatabase
import com.example.boxchat.databaselocal.entity.FriendLocal
import com.example.boxchat.databaselocal.repository.FriendLocalRepository
import com.example.boxchat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatWithFriendViewModel(application: Application) : AndroidViewModel(application) {

    val friendRef = getFriendReference()

    //list Friends
    val friend = MutableLiveData<List<User>>()
    private var friendList = mutableListOf<User>()

    //Friend in Drive
    val readAllDataFromFriend: LiveData<List<FriendLocal>>
    private val repository: FriendLocalRepository

    init {
        val friendLocalDao = FriendLocalDatabase.getFriendFromDatabase(application).friendLocalDao()
        repository = FriendLocalRepository(friendLocalDao)
        readAllDataFromFriend = repository.readAllDataFromFriend

        //get data
        getFriendList()
        addFriendLocal()
    }

    private fun getFriendReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Friends")
    }

    fun addFriend(friendLocal: FriendLocal) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFriend(friendLocal)
        }
    }

    fun addListFriend(user: List<User>) {
        friend.value = user
    }

    private fun getFriendList() {
        friendRef.child(Firebase.auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("snapshot", "User: $snapshot  ")
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        for (dataSnapShotSecond: DataSnapshot in snapshot.children) {
                            val mReceive = dataSnapShotSecond.getValue(User::class.java)
                            Log.d("ttt22", "$dataSnapShotSecond")
                            friendList.add(mReceive!!)
                            addListFriend(friendList)
                        }
                        break
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun addFriendLocal() {
        friendRef.child(Firebase.auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("snapshot", "User: $snapshot  ")
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        for (dataSnapShotSecond: DataSnapshot in snapshot.children) {
                            val mReceive = dataSnapShotSecond.getValue(FriendLocal::class.java)
                            Log.d("ttt22", "$dataSnapShotSecond")
                            addFriend(mReceive!!)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}