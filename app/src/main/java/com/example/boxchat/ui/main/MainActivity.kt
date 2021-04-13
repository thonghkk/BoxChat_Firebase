package com.example.boxchat.ui.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase
import com.example.boxchat.ui.login.LoginActivity
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.utils.CheckNetwork.Companion.getContextThis
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
        NavigationUI.setupWithNavController(mBottomNavigationView, navController)

        mFab.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        getContextThis(this)

        if (!checkNetwork()) {
            dialog()
        }
    }

    private fun dialog() {
        val mDialog = Dialog(this)
        val mLinearLayout = findViewById<LinearLayout>(R.id.mDialogNotInternet)
        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_not_internet, mLinearLayout)
        mDialog.setContentView(mDialogView)
        mDialog.show()

        mDialogView.findViewById<Button>(R.id.mBtnContinue).setOnClickListener {
            mDialog.dismiss()
        }
    }

}
