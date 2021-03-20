package com.example.boxchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.ui.login.LoginActivity

class SplashActivity : BaseActivity() {
    override fun getLayoutID() = R.layout.activity_splash

    override fun onCreateActivity(savedInstanceState: Bundle?) {
        Handler().postDelayed({
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },1500)
    }

}