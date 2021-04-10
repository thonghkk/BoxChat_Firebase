package com.example.boxchat.ui.signup

import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase.Companion.firebaseDatabase
import com.google.firebase.database.DatabaseReference

class SignUpViewModel : ViewModel() {
    var databaseReference: DatabaseReference = getUserId()

    private fun getUserId(): DatabaseReference {
        return firebaseDatabase.getReference("Users")
    }
}