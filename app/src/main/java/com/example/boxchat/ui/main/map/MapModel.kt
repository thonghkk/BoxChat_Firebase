package com.example.boxchat.ui.main.map

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.utils.CheckNetwork.Companion.context
import com.example.boxchat.commom.Firebase
import com.example.boxchat.model.MapLocation
import com.example.boxchat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class MapModel : ViewModel() {

    val mUserOnReference = getUserOnReference()
    val driverAvailable = MutableLiveData<List<MapLocation>>()
    var databaseReference = getUser()
    val me = MutableLiveData<List<User>>()
    val mUser = MutableLiveData<List<User>>()
    val userList = mutableListOf<User>()

    init {
        getYourself()
        addUser()
    }

    private fun getUserOnReference(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("driverAvailable2")
    }

    private fun getUser(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users")
    }

    fun addDriverAvailable(mapLocation: List<MapLocation>) {
        driverAvailable.value = mapLocation
    }

    fun addYourself(user: List<User>) {
        me.value = user
    }

    fun addListUser(user: List<User>) {
        mUser.value = user
    }

    private fun getYourself() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mUser = dataSnapShot.getValue(User::class.java)
                    if (mUser!!.userId == Firebase.user?.uid) {
                        userList.add(mUser)
                        addYourself(userList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addUser() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mUser = dataSnapShot.getValue(User::class.java)
                    if (mUser!!.userId != Firebase.user?.uid) {
                        userList.add(mUser)
                        addListUser(userList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}