package com.example.boxchat.ui.main.friends

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.boxchat.commom.Firebase
import com.google.firebase.database.DatabaseReference


class ChatWithFriendViewModel(application: Application) : AndroidViewModel(application) {

    val friendRef = getFriendReference()
    private fun getFriendReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Friends")
    }
}