package com.example.boxchat.base


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.boxchat.commom.Firebase
import com.example.boxchat.ui.login.LoginActivity

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
        try {
            status("offline")
        }catch (e:Exception){
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
}