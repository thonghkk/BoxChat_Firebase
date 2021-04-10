package com.example.boxchat.ui.main.setting

import android.os.Bundle
import android.widget.ImageView
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity

class InfoAppActivity : BaseActivity() {
    private lateinit var mImgBackSetting: ImageView

    override fun getLayoutID() = R.layout.activity_info_app
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mImgBackSetting = findViewById(R.id.mImgBackSetting)
        mImgBackSetting.setOnClickListener {
            onBackPressed()
        }
    }
}