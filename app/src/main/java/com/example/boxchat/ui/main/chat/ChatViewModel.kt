package com.example.boxchat.ui.main.chat

import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.google.firebase.database.DatabaseReference

class ChatViewModel:ViewModel() {
    val refChat = getFriendReference()

    private fun getFriendReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Chat")
    }
}