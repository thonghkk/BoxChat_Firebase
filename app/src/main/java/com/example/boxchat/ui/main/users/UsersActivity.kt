package com.example.boxchat.ui.main.users

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.profile.ProfileActivity
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class UsersActivity : BaseActivity() {
    private lateinit var mRecyclerUserView: RecyclerView
    private lateinit var mBack: ImageView
    private lateinit var mProfile: CircleImageView
    private lateinit var mUserViewModel: UserViewModel

    private val userList = mutableListOf<User>()

    override fun getLayoutID() = R.layout.activity_users

    @SuppressLint("WrongConstant")
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mBack = findViewById(R.id.mBtnBackMessage)
        mProfile = findViewById(R.id.mAvatarBar)
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        mBack.setOnClickListener {
            finish()
        }
        mProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        mRecyclerUserView = findViewById(R.id.mRecycleUsersView)
        mRecyclerUserView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        getUsersList()
    }


    private fun getUsersList() {
        mUserViewModel.databaseReferenceSelf.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
//                Log.d("Linked", user!!.userProfileImage)

                if (user?.userProfileImage == "") {
                    mProfile.setImageResource(R.mipmap.ic_avatar)
                } else {
                    Glide.with(this@UsersActivity)
                        .load(user?.userProfileImage)
                        .fitCenter()
                        .into(mProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        mUserViewModel.databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    Log.d("user", "User: $user ")
                    if (user!!.userId != auth.uid) {
                        userList.add(user)
                    }
                }
                mRecyclerUserView.adapter = UserAdapter(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UsersActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

}