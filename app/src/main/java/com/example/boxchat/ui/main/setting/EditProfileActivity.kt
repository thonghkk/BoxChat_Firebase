package com.example.boxchat.ui.main.setting

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase
import java.util.*
import kotlin.collections.HashMap

class EditProfileActivity : BaseActivity() {
    private lateinit var mBtnBackProfile: ImageView
    private lateinit var mEdtHomeTown: EditText
    private lateinit var mTxtBirthDay: TextView
    private lateinit var mEdtEnglishCertificate: EditText
    private lateinit var mEdtDescription: EditText
    private lateinit var mEdtName: EditText
    private lateinit var mBtnSaveInfo: TextView
    private lateinit var mSettingViewModel: SettingViewModel
    private lateinit var mImgAvatar: ImageView

    override fun getLayoutID() = R.layout.activity_edit_profile
    override fun onCreateActivity(savedInstanceState: Bundle?) {

        mTxtBirthDay = findViewById(R.id.mEdtBirthDay)
        mEdtHomeTown = findViewById(R.id.mEdtHomeTown)
        mEdtEnglishCertificate = findViewById(R.id.mEdtEnglishCertificate)
        mBtnSaveInfo = findViewById(R.id.mTxtSaveInfo)
        mBtnBackProfile = findViewById(R.id.mBtnBackProfile)
        mEdtName = findViewById(R.id.mEdtName)
        mImgAvatar = findViewById(R.id.mImgAvatar)
        mEdtDescription = findViewById(R.id.mEdtDescription)
        mSettingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)

        mTxtBirthDay.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog =
                DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        mTxtBirthDay.text = "$day / $month / $year"
                    }
                }, year, month, day)
            datePickerDialog.show()
        }
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
        hashMap["userBirthDay"] = mTxtBirthDay.text.toString()
        hashMap["userEnglishCertificate"] = mEdtEnglishCertificate.text.toString()
        hashMap["userName"] = mEdtName.text.toString()
        hashMap["userDescription"] = mEdtDescription.text.toString()
        mSettingViewModel.databaseReferenceProfile.child(Firebase.auth.uid!!)
            .updateChildren(hashMap as Map<String, Any>).addOnSuccessListener {
                Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getYourself() {
        mSettingViewModel.me.observe(this, Observer { me ->
            for (i in me) {
                mEdtHomeTown.setText(i.userHomeTown)
                mTxtBirthDay.text = i.userBirthDay
                mEdtEnglishCertificate.setText(i.userEnglishCertificate)
                mEdtName.setText(i.userName)
                mEdtDescription.setText(i.userDescription)
                Glide.with(this)
                    .load(i.userProfileImage)
                    .fitCenter()
                    .circleCrop()
                    .into(mImgAvatar)
                Log.d("TAG", "getYourself: ${i.userEnglishCertificate}")
            }
        })
    }
}