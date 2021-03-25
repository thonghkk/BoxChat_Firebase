package com.example.boxchat.ui.main.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.model.MapLocation
import com.example.boxchat.model.User
import com.google.firebase.database.DatabaseReference

class MapModel : ViewModel() {

    val mUserOnReference = getUserOnReference()
    val driverAvailable = MutableLiveData<List<MapLocation>>()
    val users = MutableLiveData<List<User>>()

    private fun getUserOnReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("driverAvailable2")
    }

    fun addDriverAvailable(mapLocation: List<MapLocation>) {
        driverAvailable.value = mapLocation
    }

    fun addListUser(user: List<User>){
        users.value = user
    }
}