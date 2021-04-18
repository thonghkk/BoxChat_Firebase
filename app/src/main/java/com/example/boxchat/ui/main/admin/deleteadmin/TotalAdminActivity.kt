package com.example.boxchat.ui.main.admin.deleteadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.R
import com.example.boxchat.ui.main.admin.AdminViewModel
import com.example.boxchat.ui.main.setting.SettingViewModel
import com.example.boxchat.ui.main.stranger.StrangerAdapter

class TotalAdminActivity : AppCompatActivity() {
    private lateinit var mRecycleAllAdminView: RecyclerView
    private lateinit var mTotalAdminViewModel: TotalAdminViewModel
    private lateinit var mSearchAllAdmin: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_admin)

        mSearchAllAdmin = findViewById(R.id.mSearchAllAdmin)
        mRecycleAllAdminView = findViewById(R.id.mRecycleAllAdminView)
        mRecycleAllAdminView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mTotalAdminViewModel = ViewModelProvider(this).get(TotalAdminViewModel::class.java)

        mTotalAdminViewModel.admins.observe(this, Observer { admin ->
            val adapter = TotalAdminAdapter(admin)
            mRecycleAllAdminView.adapter = adapter
            searchAdmin(adapter)
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