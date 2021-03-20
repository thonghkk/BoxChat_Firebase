package com.example.boxchat.ui.main.profile

import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.user
import com.google.firebase.database.DatabaseReference

class ProfileViewModel : ViewModel() {

    var databaseReferenceProfile = getUserId()

    private fun getUserId(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users").child(user?.uid!!)
    }
}