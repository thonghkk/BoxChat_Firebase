package com.example.boxchat.ui.main.friends

import android.annotation.SuppressLint
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
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.model.User
import java.util.*

class ChatWithFriendFragment : BaseFragment() {
    private lateinit var mFriendRecycleView: RecyclerView
    private lateinit var mFriendRecycleViewCircle: RecyclerView
    private lateinit var mFriendFragmentViewModel: ChatWithFriendViewModel
    private lateinit var mSearchFriends: SearchView
    private var friendList = mutableListOf<User>()

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
            mFriendRecycleView.adapter = ChatWithFriendAdapter(friendList)
        })

        mFriendFragmentViewModel.friend.observe(viewLifecycleOwner, Observer { friendList ->
            mFriendRecycleViewCircle.adapter = ChatWithFriendAdapterCircle(friendList)
        })
    }

    private fun getFriendLocal() {
        mFriendFragmentViewModel.readAllDataFromFriend.observe(
            viewLifecycleOwner,
            Observer { friend ->
                mFriendRecycleView.adapter = FriendLocalAdapter(friend)
            })
    }

    fun searchFriend(list: MutableList<User>) {
        val displayList = mutableListOf<User>()
        mSearchFriends.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    displayList.clear()
                    val search = newText.toLowerCase(Locale.getDefault())
                    list.forEach {
                        if (it.userName.toLowerCase(Locale.getDefault()).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    mFriendRecycleView.adapter!!.notifyDataSetChanged()
                } else {
                    displayList.clear()
                    displayList.addAll(list)
                    mFriendRecycleView.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })
    }
}