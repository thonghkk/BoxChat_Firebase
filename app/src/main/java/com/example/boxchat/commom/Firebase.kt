package com.example.boxchat.commom

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


open class Firebase{
    companion object{
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth.currentUser
        val firebaseDatabase:FirebaseDatabase = FirebaseDatabase.getInstance()
    }

}
