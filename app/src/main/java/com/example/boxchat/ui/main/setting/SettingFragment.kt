package com.example.boxchat.ui.main.setting

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.ui.login.LoginActivity
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork
import de.hdodenhof.circleimageview.CircleImageView

class SettingFragment : BaseFragment() {
    private lateinit var mSignOut: ImageView
    private lateinit var mAvatarSetting: CircleImageView
    private lateinit var mNameSetting: TextView
    private lateinit var mSettingProfile: LinearLayout
    private lateinit var mSettingViewModel: SettingViewModel

    override fun getLayoutID() = R.layout.fragment_setting
    override fun onViewReady(view: View) {
        mSignOut = view.findViewById(R.id.mSignOut)
        mAvatarSetting = view.findViewById(R.id.mAvatarSetting)
        mNameSetting = view.findViewById(R.id.mNameSetting)
        mSettingProfile = view.findViewById(R.id.mSettingProfile)
        mSettingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)

        mSignOut.setOnClickListener {
            status("offline")
            auth.signOut()
            mSettingViewModel.apply {
                deleteAllFriendLocal()
                deleteAllUserLocal()
                deleteAllYourself()
            }

            startActivity(Intent(context, LoginActivity::class.java))
        }

        if (checkNetwork()) {
            getYourSelf()
            mSettingProfile.setOnClickListener {
                startActivity(Intent(context, EditProfileActivity::class.java))
            }
        } else {
            getYourselfLocal()
            mSettingProfile.setOnClickListener {
                Toast.makeText(context, "Not Connect Internet !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getYourSelf() {
        mSettingViewModel.me.observe(this, Observer { me ->
            for (i in me) {
                mNameSetting.text = i.userName
                if (i.userProfileImage == "") {
                    mAvatarSetting.setImageResource(R.mipmap.ic_avatar)
                } else {
                    Glide.with(this)
                        .load(i.userProfileImage)
                        .fitCenter()
                        .circleCrop()
                        .into(mAvatarSetting)
                }
            }
        })
    }

    private fun getYourselfLocal() {
        mSettingViewModel.readAllDataFromMe.observe(
            viewLifecycleOwner, Observer { me ->
                for (i in me) {
                    mNameSetting.text = i.userName
                    if (i.userProfileImage == "") {
                        mAvatarSetting.setImageResource(R.mipmap.ic_avatar)
                    } else {
                        Glide.with(this)
                            .load(i.userProfileImage)
                            .fitCenter()
                            .circleCrop()
                            .into(mAvatarSetting)
                    }
                }
            })
    }

}