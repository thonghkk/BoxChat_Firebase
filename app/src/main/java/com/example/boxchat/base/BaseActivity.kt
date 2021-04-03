package com.example.boxchat.base


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.boxchat.commom.Firebase

abstract class BaseActivity : AppCompatActivity() {
    abstract fun getLayoutID(): Int
    abstract fun onCreateActivity(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID())
        onCreateActivity(savedInstanceState)
    }

    fun status(status:String){
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["statusExist"] = status
        Firebase.firebaseDatabase.getReference("Users").child(Firebase.auth.uid!!).updateChildren(hashMap as Map<String, Any>)
    }

    override fun onStart() {
        super.onStart()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}