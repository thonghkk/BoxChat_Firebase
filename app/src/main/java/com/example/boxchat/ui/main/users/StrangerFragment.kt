package com.example.boxchat.ui.main.users

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.model.User
import com.example.boxchat.network.FirebaseService
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging


class StrangerFragment : BaseFragment() {
    private lateinit var mRecyclerUserView: RecyclerView
    private lateinit var mUserViewModel: UserViewModel

    override fun getLayoutID() = R.layout.fragment_stranger

    @SuppressLint("WrongConstant")
    override fun onViewReady(view: View) {
        //mapping view model
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mRecyclerUserView = view.findViewById(R.id.mRecycleUsersView)
        mRecyclerUserView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        if (checkNetwork()) {
            getStranger()
        } else {
            getUserLocal()
        }
    }

    private fun getStranger(){
        mUserViewModel.users.observe(this, Observer { user ->
            mRecyclerUserView.adapter = UserAdapter(user)
            mUserViewModel.friends.observe(this, Observer { friend ->
                val sum = friend + user
                val a = sum.groupBy { it.userId }
                    .filter { it.value.size == 1 }
                    .flatMap { it.value } as MutableList<User>
                mRecyclerUserView.adapter = UserAdapter(a)

            })
        })
    }


    private fun getUserLocal() {
        mUserViewModel.readAllData.observe(viewLifecycleOwner, Observer { user ->
            mRecyclerUserView.adapter = UserLocalAdapter(user)
        })
    }

}