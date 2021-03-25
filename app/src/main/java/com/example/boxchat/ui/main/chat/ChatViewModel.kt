package com.example.boxchat.ui.main.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.model.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ChatViewModel:ViewModel() {
    val refChat = getFriendReference()
    val userRef = getUser()


    private fun getFriendReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Chat")
    }

    private fun getUser(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users")
    }


}