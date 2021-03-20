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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.commom.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.model.User
import com.example.boxchat.network.FirebaseService
import com.example.boxchat.ui.main.friends.ChatWithFriendAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging


class StrangerFragment : BaseFragment() {
    private lateinit var mRecyclerUserView: RecyclerView
    private lateinit var mUserViewModel: UserViewModel
    private val userList = mutableListOf<User>()
    private val friendList = mutableListOf<User>()

    override fun getLayoutID() = R.layout.fragment_stranger

    @SuppressLint("WrongConstant")
    override fun onViewReady(view: View) {
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mRecyclerUserView = view.findViewById(R.id.mRecycleUsersView)
        mRecyclerUserView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

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
                    val mUser = dataSnapShot.getValue(User::class.java)
                    if (mUser!!.userId != auth.uid) {
                        userList.add(mUser)
                        Log.d("user", "${mUser.userName} ")
                    }
                }

                mRecyclerUserView.adapter = UserAdapter(userList)
//
//                 mUserViewModel.friendRef.child(auth.uid!!)
//                    .addValueEventListener(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            Log.d("snapshot", "User: $snapshot  ")
//                            for (dataSnapShot: DataSnapshot in snapshot.children) {
//                                for (dataSnapShotSecond: DataSnapshot in snapshot.children) {
//                                    val mReceive = dataSnapShotSecond.getValue(User::class.java)
//                                    Log.d("ttt22", "$dataSnapShotSecond")
//                                    friendList.add(mReceive!!)
//                                }
//                                break
//                            }
//                            val a = userList.minus(friendList)
//                            mRecyclerUserView.adapter = UserAdapter(a)
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
//                        }
//                    })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

//    private fun getFriendList2(): List<User> {
//        mUserViewModel.friendRef.child(auth.uid!!)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    Log.d("snapshot", "User: $snapshot  ")
//                    for (dataSnapShot: DataSnapshot in snapshot.children) {
//                        for (dataSnapShotSecond: DataSnapshot in snapshot.children) {
//                            val mReceive = dataSnapShotSecond.getValue(User::class.java)
//                            Log.d("ttt22", "$dataSnapShotSecond")
//                            friendList.add(mReceive!!)
//                        }
//                        break
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
//                }
//            })
//        return friendList
//    }

    private fun getAddUserLocal() {
        mUserViewModel.databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mUser = dataSnapShot.getValue(UserLocal::class.java)
                    Log.d("userLocal", "User: $user ")
                    if (mUser?.userId != auth.uid) {
                        val userLocal = UserLocal(mUser?.userId!!, mUser.userName, mUser.userProfileImage)
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