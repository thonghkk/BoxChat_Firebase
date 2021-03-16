package com.example.boxchat.ui.main.users

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.commom.KeyAddFriend.Companion.CURRENT_STATE
import com.example.boxchat.commom.KeyAddFriend.Companion.FRIEND
import com.example.boxchat.commom.KeyAddFriend.Companion.NOT_SUBMIT_PENDING
import com.example.boxchat.commom.KeyAddFriend.Companion.PENDING
import com.example.boxchat.commom.KeyAddFriend.Companion.RECEIVER
import com.example.boxchat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ViewStrangerActivity : BaseActivity() {

    private lateinit var mAvatarStranger: CircleImageView
    private lateinit var mNameStranger: TextView
    private lateinit var mBtnAddFriend: Button
    private lateinit var mBtnCancelFriend: Button
    private lateinit var mUserViewModel: UserViewModel
    private var mCurrentState = "NOTHING_HAPPEN"

    override fun getLayoutID() = R.layout.activity_view_stranger
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mAvatarStranger = findViewById(R.id.mAvatarStranger)
        mNameStranger = findViewById(R.id.mNameStranger)
        mBtnAddFriend = findViewById(R.id.mBtnAddFriend)
        mBtnCancelFriend = findViewById(R.id.mBtnCancelFriend)
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val strangerId = intent.getStringExtra("userId")
        val strangerName = intent.getStringExtra("userName")
        val strangerImage = intent.getStringExtra("userImage")

        getInfoStranger(strangerId!!)
        mCheckUserExistence(strangerId)
        mBtnAddFriend.setOnClickListener {
            mPerformActions(strangerId, strangerName!!, strangerImage!!)
        }
        mBtnCancelFriend.setOnClickListener {
            mUnFriend(strangerId)
        }
    }

    private fun mCheckUserExistence(strangerId: String) {
        //if it was friend , show below
        mUserViewModel.friendRef.child(strangerId).child(user!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        mCurrentState = FRIEND
                        mBtnAddFriend.text = resources.getText(R.string.txt_send_sms)
                        mBtnCancelFriend.text = resources.getText(R.string.txt_un_friend)
                        mBtnCancelFriend.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        mUserViewModel.requestRef.child(user.uid).child(strangerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d("status", snapshot.child("status").value.toString())
                        if (snapshot.child("status").value.toString() == PENDING) {
                            mCurrentState = NOT_SUBMIT_PENDING
                            mBtnAddFriend.text =
                                resources.getText(R.string.txt_cancel_request_friend)
                            mBtnCancelFriend.visibility = View.GONE
                        }
                        Log.d("status", snapshot.child("status").value.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        mUserViewModel.requestRef.child(strangerId).child(user.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("status").value.toString() == PENDING) {
                            mCurrentState = RECEIVER
                            mBtnAddFriend.text = resources.getText(R.string.txt_accept)
                            mBtnCancelFriend.text = resources.getText(R.string.txt_cancel_friend)
                            mBtnCancelFriend.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        if (mCurrentState == CURRENT_STATE) {
            mCurrentState = CURRENT_STATE
            mBtnAddFriend.text = resources.getText(R.string.txt_send_friend_request)
            mBtnCancelFriend.visibility = View.GONE
        }
    }

    private fun getInfoStranger(strangerId: String) {
        mUserViewModel.databaseReference.child(strangerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stranger = snapshot.getValue(User::class.java)

                    mNameStranger.text = stranger!!.userName
                    if (stranger.userProfileImage == "") {
                        mAvatarStranger.setImageResource(R.mipmap.ic_avatar)
                        Log.d("Test", stranger.userProfileImage)
                    } else {
                        Glide.with(this@ViewStrangerActivity)
                            .load(stranger.userProfileImage)
                            .fitCenter()
                            .into(mAvatarStranger)
                    }
                    Log.d("NameStranger", stranger.userName)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    //handling add friend :(
    private fun mPerformActions(strangerId: String, strangerName: String, strangerImage: String) {
        //add request friend
        if (mCurrentState == CURRENT_STATE) {
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["status"] = PENDING
            mUserViewModel.requestRef.child(user!!.uid).child(strangerId).setValue(hashMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "You have send Friend request", Toast.LENGTH_SHORT)
                            .show()
                        mBtnCancelFriend.visibility = View.GONE
                        mCurrentState = NOT_SUBMIT_PENDING
                        mBtnAddFriend.text = resources.getString(R.string.txt_cancel_request_friend)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
        //unFriend Request
        if ((mCurrentState == NOT_SUBMIT_PENDING)) {
            mUserViewModel.requestRef.child(user!!.uid).child(strangerId).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "You have cancelled Friend request",
                            Toast.LENGTH_SHORT
                        ).show()
                        mCurrentState = CURRENT_STATE
                        mBtnAddFriend.text = resources.getString(R.string.txt_send_friend_request)
                        mBtnCancelFriend.visibility = View.GONE
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
        //My List friend
        if (mCurrentState == RECEIVER) {
            mUserViewModel.requestRef.child(strangerId).child(user!!.uid).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap["status"] = FRIEND
                        hashMap["nameOfFriend"] = strangerName
                        hashMap["profileImageOfFriend"] = strangerImage
                        mUserViewModel.friendRef.child(user.uid).child(strangerId).setValue(hashMap)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "You add friend",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mCurrentState = FRIEND
                                    mBtnAddFriend.text = resources.getText(R.string.txt_send_sms)
                                    mBtnCancelFriend.text =
                                        resources.getString(R.string.txt_un_friend)
                                    mBtnCancelFriend.visibility = View.VISIBLE

                                }
                            }
                    }
                }
        }
    }

    private fun mUnFriend(strangerId: String) {
        //cancel friend from receiver
        if (mCurrentState == FRIEND) {
            mUserViewModel.friendRef.child(user!!.uid).child(strangerId).removeValue()
                .addOnCompleteListener {
                    Toast.makeText(
                        this,
                        "You cancel friend",
                        Toast.LENGTH_SHORT
                    ).show()
                    mCurrentState = CURRENT_STATE
                    mBtnAddFriend.text = resources.getString(R.string.txt_send_friend_request)
                    mBtnCancelFriend.visibility = View.GONE
                }
        }
        //cancel friend from sender
        mUserViewModel.friendRef.child(strangerId).child(user!!.uid).removeValue()
            .addOnCompleteListener {
                Toast.makeText(
                    this,
                    "You cancel friend",
                    Toast.LENGTH_SHORT
                ).show()
                mCurrentState = CURRENT_STATE
                mBtnAddFriend.text = resources.getString(R.string.txt_send_friend_request)
                mBtnCancelFriend.visibility = View.GONE
            }

        //Not accept add friend
        if (mCurrentState == RECEIVER) {
            mUserViewModel.requestRef.child(strangerId).child(user.uid).removeValue()
                .addOnCompleteListener {
                    mCurrentState = CURRENT_STATE
                    mBtnAddFriend.text = resources.getString(R.string.txt_send_friend_request)
                    mBtnCancelFriend.visibility = View.GONE
                }
        }
    }
}

