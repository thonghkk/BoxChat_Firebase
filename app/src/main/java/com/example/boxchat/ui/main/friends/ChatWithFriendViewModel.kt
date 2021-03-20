package com.example.boxchat.ui.main.friends

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.boxchat.commom.Firebase
import com.example.boxchat.databaselocal.FriendLocalDatabase
import com.example.boxchat.databaselocal.entity.FriendLocal
import com.example.boxchat.databaselocal.repository.FriendLocalRepository
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatWithFriendViewModel(application: Application) : AndroidViewModel(application) {

    val friendRef = getFriendReference()

    val readAllDataFromFriend: LiveData<List<FriendLocal>>
    private val repository: FriendLocalRepository

    init {
        val friendLocalDao = FriendLocalDatabase.getFriendFromDatabase(application).friendLocalDao()
        repository = FriendLocalRepository(friendLocalDao)
        readAllDataFromFriend = repository.readAllDataFromFriend
    }

    private fun getFriendReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Friends")
    }

    fun addFriend(friendLocal: FriendLocal) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFriend(friendLocal)
        }
    }


}