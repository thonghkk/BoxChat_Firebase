package com.example.boxchat.ui.main.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.model.MapLocation
import com.google.firebase.database.DatabaseReference

class MapModel : ViewModel() {

    val friendRef = getUserOnReference()
    val driverAvailable = MutableLiveData<List<MapLocation>>()

    private fun getUserOnReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("driverAvailable2")
    }

    fun addDriverAvailable(mapLocation: List<MapLocation>) {
        driverAvailable.value = mapLocation
    }
}