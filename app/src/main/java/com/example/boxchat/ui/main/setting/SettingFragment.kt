package com.example.boxchat.ui.main.setting

import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth


class SettingFragment : BaseFragment() {
    private lateinit var mSignOut: ImageView
    private lateinit var auth: FirebaseAuth

    override fun getLayoutID() = R.layout.fragment_setting

    override fun onViewReady(view: View) {
        mSignOut = view.findViewById(R.id.mSignOut)
        auth = FirebaseAuth.getInstance()
        mSignOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context, LoginActivity::class.java))
        }

    }


}