package com.example.boxchat.ui.main.map

import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.google.firebase.database.DatabaseReference

class MapModel:ViewModel() {

    val friendRef = getUserOnReference()

    private fun getUserOnReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("driverAvailable")
    }
}