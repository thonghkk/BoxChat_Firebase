package com.example.boxchat.ui.main.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.model.MapLocation
import com.example.boxchat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class MapModel : ViewModel() {

    val mUserOnReference = getUserOnReference()
    private val mFriend = getFriends()
    val driverAvailable = MutableLiveData<List<MapLocation>>()
    val friendList = MutableLiveData<List<User>>()
    private var mUserLocation = mutableListOf<MapLocation>()
    private var mFriendList = mutableListOf<User>()

    init {
        getLocation()
        getListFriends()
    }

    private fun getUserOnReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("driverAvailable2")
    }

    private fun getFriends(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Friends").child(auth.uid!!)
    }

    fun addDriverAvailable(mapLocation: List<MapLocation>) {
        driverAvailable.value = mapLocation
    }

    fun addFriendInList(friend: List<User>) {
        friendList.value = friend
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
    //
    private fun getListFriends() {
        mFriend.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mFriends = dataSnapShot.getValue(MapLocation::class.java)
                    mFriendList.add(mFriends!!)
                }
                addFriendInList(mFriendList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}