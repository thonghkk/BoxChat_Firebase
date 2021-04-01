package com.example.boxchat.ui.login

import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.user
import com.google.firebase.database.DatabaseReference

class LoginViewModel:ViewModel() {

    var databaseReference = getUserId()
    init {
        auth
        user
        Firebase.firebaseDatabase
    }

    fun checkCurrentUser():Boolean{
        return auth.uid  != null
    }
    private fun getUserId(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users")
    }
}