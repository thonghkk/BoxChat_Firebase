package com.example.boxchat.ui.main.stranger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Constants.Companion.CHANNEL_ADD_FRIEND
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.utils.CheckNetwork.Companion.context
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.KeyAddFriend.Companion.CURRENT_STATE
import com.example.boxchat.commom.KeyAddFriend.Companion.FRIEND
import com.example.boxchat.commom.KeyAddFriend.Companion.NOT_SUBMIT_PENDING
import com.example.boxchat.commom.KeyAddFriend.Companion.PENDING
import com.example.boxchat.commom.KeyAddFriend.Companion.RECEIVER
import com.example.boxchat.model.Notification
import com.example.boxchat.model.PushNotification
import com.example.boxchat.model.User
import com.example.boxchat.network.RetrofitInstance
import com.example.boxchat.ui.main.chat.ChatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ViewStrangerActivity : BaseActivity() {
    private lateinit var mAvatarStranger: CircleImageView
    private lateinit var mNameStranger: TextView
    private lateinit var mViewHomeTown: TextView
    private lateinit var mViewBirthDay: TextView
    private lateinit var mViewEnglishCertificate: TextView
    private lateinit var mTxtDescription: TextView
    private lateinit var mBtnAddFriend: Button
    private lateinit var mBtnCancelFriend: Button
    private lateinit var mStrangerViewModel: StrangerViewModel
    private lateinit var mBtnBackStranger: ImageView
    private var mCurrentState = "NOTHING_HAPPEN"
    var topic = ""
    val userList = mutableListOf<User>()

    override fun getLayoutID() = R.layout.activity_view_stranger
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mAvatarStranger = findViewById(R.id.mAvatarStranger)
        mNameStranger = findViewById(R.id.mNameStranger)
        mBtnAddFriend = findViewById(R.id.mBtnAddFriend)
        mBtnCancelFriend = findViewById(R.id.mBtnCancelFriend)
        mViewBirthDay = findViewById(R.id.mViewBirthDay)
        mViewHomeTown = findViewById(R.id.mViewHomeTown)
        mTxtDescription = findViewById(R.id.mTxtDescription)
        mViewEnglishCertificate = findViewById(R.id.mViewEnglishCertificate)
        mBtnBackStranger = findViewById(R.id.mBtnBackStranger)
        mStrangerViewModel = ViewModelProvider(this).get(StrangerViewModel::class.java)

        val strangerId = intent.getStringExtra("userId")

        mBtnBackStranger.setOnClickListener {
            onBackPressed()
        }

        if (checkNetwork()) {
            //get user info
            mStrangerViewModel.users.observe(this, Observer { user ->
                for (i in user) {
                    if (i.userId == strangerId) {
                        mNameStranger.text = i.userName
                        mViewHomeTown.text = i.userHomeTown
                        mViewEnglishCertificate.text = i.userEnglishCertificate
                        mViewBirthDay.text = i.userBirthDay

                        if (i.userDescription == "") {
                            mTxtDescription.text =
                                getString(R.string.textView_text_description_yourself)
                        } else {
                            mTxtDescription.text = i.userDescription
                        }
                        if (i.userProfileImage == "") {
                            mAvatarStranger.setImageResource(R.mipmap.ic_avatar)
                        } else {
                            Glide.with(this@ViewStrangerActivity)
                                .load(i.userProfileImage)
                                .fitCenter()
                                .into(mAvatarStranger)
                        }
                        mBtnAddFriend.setOnClickListener {
                            mPerformActions(strangerId, i.userName, i.userProfileImage)
                            mSendSMS(strangerId, i.userName, i.userProfileImage)
                        }
                        mBtnCancelFriend.setOnClickListener {
                            mUnFriend(strangerId)
                        }
                    }
                }
            })
        } else {
            getStrangerLocal(strangerId!!)
            mAvatarStranger.setImageResource(R.mipmap.ic_avatar)
            mBtnAddFriend.setOnClickListener {
                Toast.makeText(this, "Let Connect Internet", Toast.LENGTH_SHORT).show()
            }
            mBtnCancelFriend.setOnClickListener {
                Toast.makeText(this, "Let Connect Internet", Toast.LENGTH_SHORT).show()
            }
        }
        mCheckUserExistence(strangerId!!)
    }

    private fun mCheckUserExistence(strangerId: String) {
        //status after send make friend
        //sender side
        mStrangerViewModel.requestRef.child(auth.uid!!).child(strangerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d("status", snapshot.child("status").value.toString())
                        if (snapshot.child("status").value.toString() == PENDING) {
                            mCurrentState = NOT_SUBMIT_PENDING
                            mBtnAddFriend.text =
                                resources.getText(R.string.textView_text_cancel_request_friend)
                            mBtnCancelFriend.visibility = View.GONE
                        }
                        Log.d("status", snapshot.child("status").value.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        //receiver side
        mStrangerViewModel.requestRef.child(strangerId).child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("status").value.toString() == PENDING) {
                            mCurrentState = RECEIVER
                            mBtnAddFriend.text = resources.getText(R.string.textView_text_accept)
                            mBtnCancelFriend.text =
                                resources.getText(R.string.textView_text_cancel_friend)
                            mBtnCancelFriend.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        //if it was friend , show below
        mStrangerViewModel.friendRef.child(strangerId).child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        mCurrentState = FRIEND
                        mBtnAddFriend.text = resources.getText(R.string.textView_text_send_sms)
                        mBtnCancelFriend.text = resources.getText(R.string.textView_text_un_friend)
                        mBtnCancelFriend.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        if (mCurrentState == CURRENT_STATE) {
            mCurrentState = CURRENT_STATE
            mBtnAddFriend.text = resources.getText(R.string.textView_text_send_friend_request)
            mBtnCancelFriend.visibility = View.GONE
        }
    }

    //handling add friend :(
    private fun mPerformActions(strangerId: String, strangerName: String, strangerImage: String) {
        //send make friend request
        if (mCurrentState == CURRENT_STATE) {
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["status"] = PENDING
            mStrangerViewModel.requestRef.child(auth.uid!!).child(strangerId).setValue(hashMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "You have send Friend request", Toast.LENGTH_SHORT)
                            .show()
                        mBtnCancelFriend.visibility = View.GONE
                        mCurrentState = NOT_SUBMIT_PENDING
                        mBtnAddFriend.text =
                            resources.getString(R.string.textView_text_cancel_request_friend)
                        //push notification
                        topic = "/topics/$strangerId"
                        PushNotification(
                            Notification(
                                strangerName,
                                "Send friend request", auth.uid!!, CHANNEL_ADD_FRIEND
                            ), topic
                        ).also {
                            sendNotification(it)
                        }

                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
        //unFriend Request
        if ((mCurrentState == NOT_SUBMIT_PENDING)) {
            mStrangerViewModel.requestRef.child(auth.uid!!).child(strangerId).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "You have cancelled Friend request",
                            Toast.LENGTH_SHORT
                        ).show()
                        mCurrentState = CURRENT_STATE
                        mBtnAddFriend.text =
                            resources.getString(R.string.textView_text_send_friend_request)
                        mBtnCancelFriend.visibility = View.GONE
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            mStrangerViewModel.requestRef.child(auth.uid!!).child(strangerId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            mCurrentState = CURRENT_STATE
                            mBtnAddFriend.text = resources.getString(R.string.textView_text_send_friend_request)
                            mBtnCancelFriend.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })


        }
        //if receiver accept , do below
        if (mCurrentState == RECEIVER) {
            mStrangerViewModel.requestRef.child(strangerId).child(auth.uid!!).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap["userId"] = strangerId
                        hashMap["status"] = FRIEND

                        val hashMap2: HashMap<String, String> = HashMap()
                        hashMap2["userId"] = auth.uid!!
                        hashMap2["status"] = FRIEND

                        mStrangerViewModel.friendRef.child(auth.uid!!).child(strangerId)
                            .setValue(hashMap)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "You add friend",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mCurrentState = FRIEND
                                    mBtnAddFriend.text =
                                        resources.getText(R.string.textView_text_send_sms)
                                    mBtnCancelFriend.text =
                                        resources.getString(R.string.textView_text_un_friend)
                                    mBtnCancelFriend.visibility = View.VISIBLE
                                }
                            }

                        mStrangerViewModel.friendRef.child(strangerId).child(auth.uid!!)
                            .setValue(hashMap2)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "You add friend",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mCurrentState = FRIEND
                                    mBtnAddFriend.text =
                                        resources.getText(R.string.textView_text_send_sms)
                                    mBtnCancelFriend.text =
                                        resources.getString(R.string.textView_text_un_friend)
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
            mStrangerViewModel.friendRef.child(auth.uid!!).child(strangerId).removeValue()
                .addOnCompleteListener {
                    Toast.makeText(
                        this,
                        "You cancel friend",
                        Toast.LENGTH_SHORT
                    ).show()
                    mCurrentState = CURRENT_STATE
                    mBtnAddFriend.text =
                        resources.getString(R.string.textView_text_send_friend_request)
                    mBtnCancelFriend.visibility = View.GONE
                }

            //cancel friend from sender
            mStrangerViewModel.friendRef.child(strangerId).child(auth.uid!!).removeValue()
                .addOnCompleteListener {
                    Toast.makeText(
                        this,
                        "You cancel friend",
                        Toast.LENGTH_SHORT
                    ).show()
                    mCurrentState = CURRENT_STATE
                    mBtnAddFriend.text = resources.getString(R.string.textView_text_send_friend_request)
                    mBtnCancelFriend.visibility = View.GONE
                }


            mStrangerViewModel.friendRef.child(strangerId).child(auth.uid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            mCurrentState = CURRENT_STATE
                            mBtnAddFriend.text = resources.getString(R.string.textView_text_send_friend_request)
                            mBtnCancelFriend.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

        }

        //Not accept add friend
        if (mCurrentState == RECEIVER) {
            mStrangerViewModel.requestRef.child(strangerId).child(auth.uid!!).removeValue()
                .addOnCompleteListener {
                    mCurrentState = CURRENT_STATE
                    mBtnAddFriend.text =
                        resources.getString(R.string.textView_text_send_friend_request)
                    mBtnCancelFriend.visibility = View.GONE
                }
        }
    }

    private fun getStrangerLocal(strangerId: String) {
        mStrangerViewModel.readAllData.observe(this, Observer { stranger ->
            Log.d("stranger", "$stranger")
            for (i in stranger) {
                if (i.userId == strangerId) {
                    mNameStranger.text = i.userName
                }
            }
        })
    }

    private fun mSendSMS(strangerId: String, strangerName: String, strangerImage: String) {
        if (mCurrentState == FRIEND) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId", strangerId)
            intent.putExtra("userName", strangerName)
            intent.putExtra("userImage", strangerImage)
            context.startActivity(intent)
        }
    }

    //sent make friend and show notification on Receiver
    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.notificationApi.postNotification(notification)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ViewStrangerActivity,
                        " Response : ${Gson().toJson(response)}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@ViewStrangerActivity,
                        response.errorBody().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
//                Toast.makeText(this@ChatActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

