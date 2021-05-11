package com.example.boxchat.ui.main.stranger

import android.annotation.SuppressLint
import android.util.Log
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
import com.example.boxchat.model.User

class StrangerFragment : BaseFragment() {
    private lateinit var mRecyclerUserView: RecyclerView
    private lateinit var mStrangerViewModel: StrangerViewModel
    private lateinit var mSearchStranger: SearchView
    private lateinit var strangerAdapter: StrangerAdapter
    private lateinit var mTxtConnect: TextView

    override fun getLayoutID() = R.layout.fragment_stranger

    @SuppressLint("WrongConstant")
    override fun onViewReady(view: View) {
        mSearchStranger = view.findViewById(R.id.mSearchStranger)
        mTxtConnect = view.findViewById(R.id.mTxtConnect)
        //mapping view model
        mStrangerViewModel = ViewModelProvider(this).get(StrangerViewModel::class.java)
        mRecyclerUserView = view.findViewById(R.id.mRecycleUsersView)
        mRecyclerUserView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        val list = mutableListOf<User>()

        if (checkNetwork()) {
            getStranger()
        } else {
            mTxtConnect.visibility = View.VISIBLE
        }
    }

    private fun getStranger() {
        mStrangerViewModel.users.observe(this, Observer { user ->
            strangerAdapter = StrangerAdapter(user)
            mRecyclerUserView.adapter = strangerAdapter
            searchStranger(strangerAdapter)
            mStrangerViewModel.friends.observe(this, Observer { friend ->
                val sum = friend + user
                val a = sum.groupBy { it.userId }
                    .filter { it.value.size == 1 }
                    .flatMap { it.value } as MutableList<User>
                strangerAdapter = StrangerAdapter(a)
                mRecyclerUserView.adapter = strangerAdapter
                //search stranger
                searchStranger(strangerAdapter)
                strangerAdapter.notifyDataSetChanged()
            })
        })
    }

    private fun searchStranger(strangerAdapter: StrangerAdapter) {
        mSearchStranger.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                strangerAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                strangerAdapter.filter.filter(newText)
                return false
            }
        })
    }
}