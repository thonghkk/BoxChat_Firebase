package com.example.boxchat.commom

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class Firebase {
    companion object {
        //get auth on Firebase
        var auth: FirebaseAuth = FirebaseAuth.getInstance()

        //get user on local
        val user: FirebaseUser? = auth.currentUser

        //import firebaseDatabase
        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    }
}
