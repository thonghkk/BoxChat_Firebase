package com.example.boxchat.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.adapter.UserAdapter
import com.example.boxchat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class UsersActivity : AppCompatActivity() {
    private lateinit var mRecyclerUserView: RecyclerView
    private val userList = mutableListOf<User>()
    private lateinit var mBack:ImageView
    private lateinit var mProfile:CircleImageView

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        mBack = findViewById(R.id.mBtnBackMessage)
        mProfile = findViewById(R.id.mAvatarBar)

        mBack.setOnClickListener{
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
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        val databaseReference2 =
            FirebaseDatabase.getInstance().getReference("Users").child(firebase.uid)

        databaseReference2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                Log.d("Linked", user!!.userProfileImage)

                if (user.userProfileImage == ""){
                    mProfile.setImageResource(R.mipmap.ic_avatar)
                }else{
                    Glide.with(this@UsersActivity)
                        .load(user.userProfileImage)
                        .fitCenter()
                        .into(mProfile)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    Log.d("user", "User: $user ")
                    if (user!!.userId != firebase.uid) {
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