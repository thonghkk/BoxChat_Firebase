package com.example.boxchat.ui.main.users

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.boxchat.utils.CheckNetwork.Companion.context
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.firebaseDatabase
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.databaselocal.repository.UserLocalRepository
import com.example.boxchat.databaselocal.UserLocalDatabase
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    var databaseReference = getUserId()
    val requestRef = getRequestReference()
    val friendRef = getFriendReference()
    val readAllData: LiveData<List<UserLocal>>
    val users = MutableLiveData<List<User>>()
    val friends = MutableLiveData<List<User>>()
    private var friendList = mutableListOf<User>()
    private val userList = mutableListOf<User>()
    private val repository: UserLocalRepository

    init {
        val userLocalDao = UserLocalDatabase.getDatabase(application).userLocalDao()
        repository = UserLocalRepository(userLocalDao)
        readAllData = repository.readAllData
        //init friend
        getFriendList()
        getUsersList()
        getAddUserLocal()
    }

    fun addUser(userLocal: UserLocal) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(userLocal)
        }
    }

    private fun getUserId(): DatabaseReference {
        return firebaseDatabase.getReference("Users")
    }

    private fun getRequestReference(): DatabaseReference {
        return firebaseDatabase.getReference("Requests")
    }

    private fun getFriendReference(): DatabaseReference {
        return firebaseDatabase.getReference("Friends")
    }

    fun addListUser(user: List<User>) {
        users.value = user
    }


    fun addListFriend(user: List<User>) {
        friends.value = user
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

    private fun getUsersList() {
        val userId = Firebase.auth.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userId")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mUser = dataSnapShot.getValue(User::class.java)
                    if (mUser!!.userId != userId) {
                        userList.add(mUser)
                        addListUser(userList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getAddUserLocal() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mUser = dataSnapShot.getValue(UserLocal::class.java)
                    Log.d("userLocal", "User: $user ")
                    if (mUser?.userId != Firebase.auth.uid) {
                        val userLocal =
                            UserLocal(mUser?.userId!!, mUser.userName, mUser.userProfileImage)
                        addUser(userLocal)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


}
