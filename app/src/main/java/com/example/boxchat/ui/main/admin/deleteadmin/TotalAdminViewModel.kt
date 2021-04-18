package com.example.boxchat.ui.main.admin.deleteadmin

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

class TotalAdminViewModel : ViewModel() {

    private val adminRef = getAdminList()
    private val userRef = getUserId()

    private val adminList = mutableListOf<User>()

    //custom Live Data
    val admins = MutableLiveData<List<User>>()

    init {
        getAllAdmin()
    }

    fun addListAdmin(admin: List<User>) {
        admins.value = admin
    }


    private fun getAdminList(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Admin")
    }

    private fun getUserId(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users")
    }

    private fun getAllAdmin() {
        adminRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adminList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val admin = dataSnapshot.getValue(User::class.java)
                    if (admin?.userId != auth.uid){
                        //userList
                        userRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapShot: DataSnapshot in snapshot.children) {
                                    val mUser = dataSnapShot.getValue(User::class.java)
                                    if (mUser!!.userId == admin?.userId) {
                                        adminList.add(mUser)
                                    }
                                }
                                addListAdmin(adminList)
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}