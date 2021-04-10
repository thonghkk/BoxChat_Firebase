package com.example.boxchat.ui.main.stranger

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
import com.example.boxchat.databaselocal.FriendLocalDatabase
import com.example.boxchat.databaselocal.repository.UserLocalRepository
import com.example.boxchat.databaselocal.UserLocalDatabase
import com.example.boxchat.databaselocal.entity.FriendLocal
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.databaselocal.repository.FriendLocalRepository
import com.example.boxchat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StrangerViewModel(application: Application) : AndroidViewModel(application) {

    var databaseReference = getUserId()
    val requestRef = getRequestReference()
    val friendRef = getFriendReference()
    val readAllData: LiveData<List<UserLocal>>
    private val repository: UserLocalRepository
    val readAllDataFromFriend: LiveData<List<FriendLocal>>
    private val repositoryFriendLocal: FriendLocalRepository

    val users = MutableLiveData<List<User>>()
    val friends = MutableLiveData<List<User>>()
    private var friendList = mutableListOf<User>()
    private val userList = mutableListOf<User>()

    init {
        //initialization user on local
        val userLocalDao = UserLocalDatabase.getDatabase(application).userLocalDao()
        repository = UserLocalRepository(userLocalDao)
        readAllData = repository.readAllData
        //initialization friend on local
        val friendLocalDao = FriendLocalDatabase.getFriendFromDatabase(application).friendLocalDao()
        repositoryFriendLocal = FriendLocalRepository(friendLocalDao)
        readAllDataFromFriend = repositoryFriendLocal.readAllDataFromFriend
        //init friend
        getFriendList()
        getUsersList()
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
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getUsersList() {
        val userId = Firebase.auth.uid
        //  FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userId")
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
}
