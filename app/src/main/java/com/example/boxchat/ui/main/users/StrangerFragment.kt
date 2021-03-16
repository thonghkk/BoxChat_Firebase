package com.example.boxchat.ui.main.users

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.commom.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.firebaseDatabase
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.model.User
import com.example.boxchat.network.FirebaseService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging


class StrangerFragment : BaseFragment() {
    private lateinit var mRecyclerUserView: RecyclerView
    private lateinit var mUserViewModel: UserViewModel
    private val userList = mutableListOf<User>()

    override fun getLayoutID() = R.layout.fragment_stranger

    @SuppressLint("WrongConstant")
    override fun onViewReady(view: View) {
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mRecyclerUserView = view.findViewById(R.id.mRecycleUsersView)
        mRecyclerUserView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        //get value SharedPreferences
        FirebaseService.sharePref =
            this.activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            FirebaseService.token = it.result.token
            Log.d("token", it.result.token)
        }

        getAddUserLocal()
        if (checkNetwork()) {
            getUsersList()
        } else {
            getUserLocal()
        }
    }

    private fun getUsersList() {
        val userId = auth.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userId")

        mUserViewModel.databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)
                    Log.d("user", "User: $user ")
                    if (user!!.userId != auth.uid) {
                        userList.add(user)
                    }
                }
                mRecyclerUserView.adapter = UserAdapter(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getAddUserLocal() {
        mUserViewModel.databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(UserLocal::class.java)
                    Log.d("userLocal", "User: $user ")
                    if (user!!.userId != auth.uid) {
                        val userLocal = UserLocal(user.userId, user.userName, user.userName)
                        mUserViewModel.addUser(userLocal)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserLocal() {
        mUserViewModel.readAllData.observe(viewLifecycleOwner, Observer { user ->
            mRecyclerUserView.adapter = UserLocalAdapter(user)
        })
    }
}