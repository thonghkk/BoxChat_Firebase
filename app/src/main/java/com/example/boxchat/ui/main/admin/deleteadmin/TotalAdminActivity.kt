package com.example.boxchat.ui.main.admin.deleteadmin

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase
import com.example.boxchat.model.User
import com.example.boxchat.ui.login.LoginActivity
import com.example.boxchat.ui.main.admin.AdminViewModel
import com.example.boxchat.ui.main.setting.SettingViewModel
import com.example.boxchat.ui.main.stranger.StrangerAdapter

class TotalAdminActivity : BaseActivity() {
    private lateinit var mRecycleAllAdminView: RecyclerView
    private lateinit var mTotalAdminViewModel: TotalAdminViewModel
    private lateinit var mSearchAllAdmin: SearchView
    val listUser = mutableListOf<User>()

    override fun getLayoutID() = R.layout.activity_total_admin

    override fun onCreateActivity(savedInstanceState: Bundle?) {

        mSearchAllAdmin = findViewById(R.id.mSearchAllAdmin)
        mRecycleAllAdminView = findViewById(R.id.mRecycleAllAdminView)
        mRecycleAllAdminView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mTotalAdminViewModel = ViewModelProvider(this).get(TotalAdminViewModel::class.java)

        mTotalAdminViewModel.admins.observe(this, Observer { admin ->
            mTotalAdminViewModel.users.observe(this, Observer { users ->
                listUser.clear()
                for (i in admin){
                    for (j in users){
                        if (i.userId == j.userId){
                            listUser.add(j)
                        }
                    }
                }
                val adapter = TotalAdminAdapter(listUser)
                mRecycleAllAdminView.adapter = adapter
                searchAdmin(adapter)
            })

        })
    }

    private fun searchAdmin(totalAdminAdapter: TotalAdminAdapter) {
        mSearchAllAdmin.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                totalAdminAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                totalAdminAdapter.filter.filter(newText)
                return false
            }
        })
    }

}