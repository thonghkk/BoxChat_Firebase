package com.example.boxchat.ui.main.setting

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity

class InfoAppActivity : BaseActivity() {
    private lateinit var mImgBackSetting: ImageView
    private lateinit var mSettingViewModel: SettingViewModel

    override fun getLayoutID() = R.layout.activity_info_app
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mImgBackSetting = findViewById(R.id.mImgBackSetting)
        mSettingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)

        mImgBackSetting.setOnClickListener {
            onBackPressed()
        }
    }
}