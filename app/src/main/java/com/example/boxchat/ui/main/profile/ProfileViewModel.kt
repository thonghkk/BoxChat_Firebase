package com.example.boxchat.ui.main.profile

import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.google.firebase.database.DatabaseReference

class ProfileViewModel: ViewModel() {

    var databaseReference3 = getUserId()

    private fun getUserId(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users")
    }
}