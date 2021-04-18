package com.example.boxchat.ui.main.admin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.ui.main.MainActivity
import com.example.boxchat.ui.main.admin.deleteadmin.TotalAdminActivity
import com.example.boxchat.ui.main.admin.newadmin.ManagerUserActivity

class AdminActivity : BaseActivity() {
    private lateinit var mAdminViewModel: AdminViewModel
    private lateinit var mNameAdmin: TextView
    private lateinit var mTxtTotalUser: TextView
    private lateinit var mTxtTotalChat: TextView
    private lateinit var mTxtTotalAdmin: TextView
    private lateinit var mImageAdmin: ImageView
    private lateinit var mBackHome: ImageView
    private lateinit var mManagerUser: LinearLayout
    private lateinit var mManagerAdmin: LinearLayout
    override fun getLayoutID() = R.layout.activity_admin

    override fun onCreateActivity(savedInstanceState: Bundle?) {

        mNameAdmin = findViewById(R.id.mNameAdmin)
        mImageAdmin = findViewById(R.id.mImageAdmin)
        mTxtTotalUser = findViewById(R.id.mTxtTotalUser)
        mTxtTotalChat = findViewById(R.id.mTxtTotalChat)
        mBackHome = findViewById(R.id.mBackHome)
        mManagerUser = findViewById(R.id.mManagerUser)
        mManagerAdmin = findViewById(R.id.mManagerAdmin)
        mTxtTotalAdmin = findViewById(R.id.mTxtTotalAdmin)

        mAdminViewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        mBackHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        mManagerUser.setOnClickListener {
            startActivity(Intent(this,ManagerUserActivity::class.java))
        }

        mManagerAdmin.setOnClickListener {
            startActivity(Intent(this,TotalAdminActivity::class.java))
        }

        mAdminViewModel.me.observe(this, Observer { admin ->
            for (i in admin) {
                mNameAdmin.text = "Hello, ${i.userName}"
                Glide.with(this)
                    .load(i.userProfileImage)
                    .fitCenter()
                    .circleCrop()
                    .into(mImageAdmin)
            }
        })

        mAdminViewModel.userList.observe(this, Observer { users ->
            mTxtTotalUser.text = "Total Of User : ${users.size}"
        })

        mAdminViewModel.chatList.observe(this, Observer { chat ->
            mTxtTotalChat.text = "Total Of Chat : ${chat.size}"
        })
        mAdminViewModel.admins.observe(this, Observer { admin->
            mTxtTotalAdmin.text = "Total of Admin : ${admin.size}"
        })
    }

}