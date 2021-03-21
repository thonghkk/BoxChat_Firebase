package com.example.boxchat.ui.main.friends

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.commom.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.databaselocal.entity.FriendLocal
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class ChatWithFriendFragment : BaseFragment() {
    private lateinit var mFriendRecycleView: RecyclerView
    private lateinit var mFriendFragmentViewModel: ChatWithFriendViewModel
    private lateinit var mSearchFriends: SearchView
    private var friendList = mutableListOf<User>()

    override fun getLayoutID() = R.layout.fragment_chat

    @SuppressLint("WrongConstant")
    override fun onViewReady(view: View) {
        mFriendRecycleView = view.findViewById(R.id.mFriendRecycleView)
        mSearchFriends = view.findViewById(R.id.mSearchFriends)
        mFriendRecycleView.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        mFriendFragmentViewModel = ViewModelProvider(this).get(ChatWithFriendViewModel::class.java)

        addFriendLocal()
        getFriendList()
        if (checkNetwork()) {
            mFriendFragmentViewModel.friend.observe(viewLifecycleOwner, Observer { friendList ->
                mFriendRecycleView.adapter = ChatWithFriendAdapter(friendList)
            })
        } else {
            getFriendLocal()
        }
    }

    private fun getFriendList() {
        mFriendFragmentViewModel.friendRef.child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("snapshot", "User: $snapshot  ")
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        for (dataSnapShotSecond: DataSnapshot in snapshot.children) {
                            val mReceive = dataSnapShotSecond.getValue(User::class.java)
                            Log.d("ttt22", "$dataSnapShotSecond")
                            friendList.add(mReceive!!)
                            mFriendFragmentViewModel.addListFriend(friendList)
                        }
                        break
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun addFriendLocal() {
        mFriendFragmentViewModel.friendRef.child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("snapshot", "User: $snapshot  ")
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        for (dataSnapShotSecond: DataSnapshot in snapshot.children) {
                            val mReceive = dataSnapShotSecond.getValue(FriendLocal::class.java)
                            Log.d("ttt22", "$dataSnapShotSecond")
                            mFriendFragmentViewModel.addFriend(mReceive!!)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
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