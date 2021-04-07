package com.example.boxchat.ui.main.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.model.MapLocation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class MapModel : ViewModel() {

    val mUserOnReference = getUserOnReference()
    val driverAvailable = MutableLiveData<List<MapLocation>>()
    private var mUserLocation = mutableListOf<MapLocation>()

    init {
        getLocation()
    }

    private fun getUserOnReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("driverAvailable2")
    }

    fun addDriverAvailable(mapLocation: List<MapLocation>) {
        driverAvailable.value = mapLocation
    }

    //get Location of user
    private fun getLocation() {
        mUserOnReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mLocation = dataSnapShot.getValue(MapLocation::class.java)
                    if (mLocation!!.userId != Firebase.auth.uid) {
                        mUserLocation.add(mLocation)
                        addDriverAvailable(mUserLocation)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}