package com.example.boxchat.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.ui.main.map.MapsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : BaseActivity() {
    private lateinit var mBottomNavigationView: BottomNavigationView
    private lateinit var mFab: FloatingActionButton


    override fun getLayoutID() = R.layout.activity_main

    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mBottomNavigationView = findViewById(R.id.mBottomNavigationView)
        mFab = findViewById(R.id.mFab)

        mBottomNavigationView.background = null
     // mBottomNavigationView.menu.getItem(3).isEnabled = false

        val navController = findNavController(R.id.mFragment)
        NavigationUI.setupWithNavController(mBottomNavigationView,navController)

        mFab.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

    }

}