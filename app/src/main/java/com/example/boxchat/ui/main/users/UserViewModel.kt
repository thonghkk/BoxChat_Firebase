package com.example.boxchat.ui.main.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.boxchat.commom.Firebase.Companion.firebaseDatabase
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.databaselocal.repository.UserLocalRepository
import com.example.boxchat.databaselocal.UserLocalDatabase
import com.example.boxchat.databaselocal.entity.UserLocal

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    var databaseReference = getUserId()
    val requestRef = getRequestReference()
    val friendRef = getFriendReference()
    val readAllData: LiveData<List<UserLocal>>
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
}
