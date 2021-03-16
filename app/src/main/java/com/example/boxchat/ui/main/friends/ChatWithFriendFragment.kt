package com.example.boxchat.ui.main.friends

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.users.UserAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class ChatWithFriendFragment:BaseFragment() {

    private lateinit var mFriendRecycleView:RecyclerView
    private lateinit var mFriendFragmentViewModel: ChatWithFriendViewModel
    private val friendList = mutableListOf<User>()

    override fun getLayoutID() = R.layout.fragment_chat

    override fun onViewReady(view: View) {
        mFriendRecycleView = view.findViewById(R.id.mFriendRecycleView)
        mFriendFragmentViewModel = ViewModelProvider(this).get(ChatWithFriendViewModel::class.java)
        getFriendList()
    }

    private fun getFriendList() {
        val userId = user!!.uid

        mFriendFragmentViewModel.friendRef.child(user.uid)
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)
                    Log.d("friend", "User: $user ;;; ${Firebase.user.uid} ")
                    Log.d("id",userId.toString())
                    if (user!!.userId != userId) {
                        friendList.add(user)
                    }
                }
                mFriendRecycleView.adapter = UserAdapter(friendList)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}