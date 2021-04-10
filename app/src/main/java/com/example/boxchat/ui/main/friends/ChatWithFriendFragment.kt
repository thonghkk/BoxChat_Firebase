package com.example.boxchat.ui.main.friends

import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork

class ChatWithFriendFragment : BaseFragment() {
    private lateinit var mFriendRecycleView: RecyclerView
    private lateinit var mFriendRecycleViewCircle: RecyclerView
    private lateinit var mFriendFragmentViewModel: ChatWithFriendViewModel
    private lateinit var mSearchFriends: SearchView
    private lateinit var friendAdapter: ChatWithFriendAdapter
    private lateinit var friendAdapterCircle: ChatWithFriendAdapterCircle
    private lateinit var mTxtConnectChat: TextView

    override fun getLayoutID() = R.layout.fragment_chat

    @SuppressLint("WrongConstant")
    override fun onViewReady(view: View) {
        mFriendRecycleView = view.findViewById(R.id.mFriendRecycleView)
        mTxtConnectChat = view.findViewById(R.id.mTxtConnectChat)
        mFriendRecycleViewCircle = view.findViewById(R.id.mFriendRecycleViewCircle)
        mSearchFriends = view.findViewById(R.id.mSearchFriends)
        mFriendRecycleView.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        mFriendRecycleViewCircle.layoutManager =
            LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        mFriendFragmentViewModel = ViewModelProvider(this).get(ChatWithFriendViewModel::class.java)

        if (checkNetwork()) {
            getFriend()
        } else {
            getFriendLocal()
            mTxtConnectChat.visibility = View.VISIBLE
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