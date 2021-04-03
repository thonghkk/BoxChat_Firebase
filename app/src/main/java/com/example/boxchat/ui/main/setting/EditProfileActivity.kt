package com.example.boxchat.ui.main.setting

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase
import com.example.boxchat.ui.main.profile.ProfileViewModel

class EditProfileActivity : BaseActivity() {

    private lateinit var mBtnBackProfile: ImageView
    private lateinit var mEdtHomeTown: EditText
    private lateinit var mEdtBirthDay: EditText
    private lateinit var mEdtEnglishCertificate: EditText
    private lateinit var mBtnSaveInfo: Button
    private lateinit var mSettingViewModel: SettingViewModel


    override fun getLayoutID() = R.layout.fragment_edit_profile
    override fun onCreateActivity(savedInstanceState: Bundle?) {

        mEdtBirthDay = findViewById(R.id.mEdtBirthDay)
        mEdtHomeTown = findViewById(R.id.mEdtHomeTown)
        mEdtEnglishCertificate = findViewById(R.id.mEdtEnglishCertificate)
        mBtnSaveInfo = findViewById(R.id.mBtnSaveInfo)
        mBtnBackProfile = findViewById(R.id.mBtnBackProfile)
        mSettingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)


        getYourself()
        mBtnBackProfile.setOnClickListener {
            onBackPressed()
        }
        mBtnSaveInfo.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["userHomeTown"] = mEdtHomeTown.text.toString()
        hashMap["userBirthDay"] = mEdtBirthDay.text.toString()
        hashMap["userEnglishCertificate"] = mEdtEnglishCertificate.text.toString()
        mSettingViewModel.databaseReferenceProfile.child(Firebase.auth.uid!!)
            .updateChildren(hashMap as Map<String, Any>).addOnSuccessListener {
                Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getYourself() {
        mSettingViewModel.me.observe(this, Observer { me ->
            for (i in me) {
                mEdtHomeTown.setText(i.userHomeTown)
                mEdtBirthDay.setText(i.userBirthDay)
                mEdtEnglishCertificate.setText(i.userEnglishCertificate)
                Log.d("TAG", "getYourself: ${i.userEnglishCertificate}")
            }
        })
    }

}