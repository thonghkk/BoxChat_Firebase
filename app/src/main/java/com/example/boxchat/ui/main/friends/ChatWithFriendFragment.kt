package com.example.boxchat.ui.main.friends

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.model.User
import okhttp3.internal.notify
import java.util.*
import kotlin.collections.HashMap

class ChatWithFriendFragment : BaseFragment() {
    private lateinit var mFriendRecycleView: RecyclerView
    private lateinit var mFriendRecycleViewCircle: RecyclerView
    private lateinit var mFriendFragmentViewModel: ChatWithFriendViewModel
    private lateinit var mSearchFriends: SearchView
    private lateinit var friendAdapter: ChatWithFriendAdapter
    private lateinit var friendAdapterCircle: ChatWithFriendAdapterCircle

    override fun getLayoutID() = R.layout.fragment_chat

    @SuppressLint("WrongConstant")
    override fun onViewReady(view: View) {
        mFriendRecycleView = view.findViewById(R.id.mFriendRecycleView)
        mFriendRecycleViewCircle = view.findViewById(R.id.mFriendRecycleViewCircle)
        mSearchFriends = view.findViewById(R.id.mSearchFriends)
        mFriendRecycleView.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        mFriendRecycleViewCircle.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        mFriendFragmentViewModel = ViewModelProvider(this).get(ChatWithFriendViewModel::class.java)

        if (checkNetwork()) {
            getFriend()
        } else {
            getFriendLocal()
        }
    }

    private fun getFriend() {
        mFriendFragmentViewModel.friend.observe(viewLifecycleOwner, Observer { friendList ->
            friendAdapter = ChatWithFriendAdapter(friendList)
            friendAdapterCircle = ChatWithFriendAdapterCircle((friendList))

            mFriendRecycleView.adapter = friendAdapter
            mFriendRecycleViewCircle.adapter = friendAdapterCircle

            searchFriend(friendAdapter, friendAdapterCircle)
            friendAdapter.notifyDataSetChanged()
            friendAdapterCircle.notifyDataSetChanged()
        })

    }

    private fun getFriendLocal() {
        mFriendFragmentViewModel.readAllDataFromFriend.observe(
            viewLifecycleOwner,
            Observer { friend ->
                val mFriendLocalAdapter = FriendLocalAdapter(friend)
                mFriendRecycleView.adapter = mFriendLocalAdapter
                mFriendLocalAdapter.notifyDataSetChanged()
            })
    }

    private fun searchFriend(
        friendAdapter: ChatWithFriendAdapter,
        friendAdapterCircle: ChatWithFriendAdapterCircle
    ) {
        mSearchFriends.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                friendAdapter.filter.filter(query)
                friendAdapterCircle.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                friendAdapter.filter.filter(newText)
                friendAdapterCircle.filter.filter(newText)
                return false
            }
        })
    }

}