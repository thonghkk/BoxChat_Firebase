package com.example.boxchat.ui.main.admin.newadmin

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.admin.deleteadmin.TotalAdminAdapter

class ManagerUserActivity : BaseActivity() {
    private lateinit var mRecycleViewNewAdmin: RecyclerView
    private lateinit var mManagerUserViewModel: ManagerUserViewModel
    private lateinit var mSearchAllUser: SearchView
    override fun getLayoutID() = R.layout.activity_manager_user

    @SuppressLint("WrongConstant")
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mSearchAllUser = findViewById(R.id.mSearchAllUser)
        mRecycleViewNewAdmin = findViewById(R.id.mRecycleAdminView)
        mManagerUserViewModel = ViewModelProvider(this).get(ManagerUserViewModel::class.java)
        mRecycleViewNewAdmin.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        mManagerUserViewModel.users.observe(this, Observer { user ->
            mManagerUserViewModel.admins.observe(this, Observer { admin ->
                val sum = user + admin
                val a = sum.groupBy { it.userId }
                    .filter { it.value.size == 1 }
                    .flatMap { it.value } as MutableList<User>
                val adapter = ManagerUserAdapter(a)
                mRecycleViewNewAdmin.adapter = adapter

                searchUser(adapter)
            })
        })
    }

    private fun searchUser(mManagerUserAdapter: ManagerUserAdapter) {
        mSearchAllUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mManagerUserAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mManagerUserAdapter.filter.filter(newText)
                return false
            }
        })
    }
}