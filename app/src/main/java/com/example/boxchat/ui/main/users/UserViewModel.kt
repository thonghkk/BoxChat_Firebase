package com.example.boxchat.ui.main.users

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
    val stranger = MutableLiveData<List<User>>()

    private val userList = mutableListOf<User>()
    private val repository: UserLocalRepository

    init {
        val userLocalDao = UserLocalDatabase.getDatabase(application).userLocalDao()
        repository = UserLocalRepository(userLocalDao)
        readAllData = repository.readAllData
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


    fun addListUser(user: List<User>){
        users.value = user
    }

    fun addListFriend(user: List<User>){
        friends.value = user
    }
    fun addStranger(user: List<User>){
        stranger.value = user
    }


}
