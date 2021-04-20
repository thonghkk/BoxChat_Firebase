package com.example.boxchat.ui.main.admin.newadmin

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.model.User
import com.example.boxchat.utils.CheckNetwork
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ManagerUserViewModel : ViewModel() {

    private val databaseReference = getUserId()
    private val adminRef = getAdminList()
    private val userList = mutableListOf<User>()
    private val adminList = mutableListOf<User>()

    val users = MutableLiveData<List<User>>()
    val admins = MutableLiveData<List<User>>()

    init {
        getListUser()
        getListAdmin()
    }

    private fun getUserId(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users")
    }

    private fun getAdminList(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Admin")
    }

    fun addListUser(user: List<User>) {
        users.value = user
    }

    fun addListAdmin(admin: List<User>) {
        admins.value = admin
    }

    private fun getListUser() {
        val userId = Firebase.auth.uid
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mUser = dataSnapShot.getValue(User::class.java)
                    if (mUser!!.userId != userId) {
                        userList.add(mUser)
                        addListUser(userList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(CheckNetwork.context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getListAdmin() {
        adminRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val admin = dataSnapshot.getValue(User::class.java)
                    if (admin?.userId != auth.uid){
                        adminList.add(admin!!)
                    }
                }
                addListAdmin(adminList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}