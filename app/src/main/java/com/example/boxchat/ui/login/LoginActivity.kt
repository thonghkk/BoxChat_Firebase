package com.example.boxchat.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.boxchat.ui.main.MainActivity
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.utils.CheckNetwork.Companion.getContextThis
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.ui.signup.SignUpActivity

class LoginActivity : BaseActivity() {

    private lateinit var mBtnLogin: Button
    private lateinit var mBtnSignUp: Button
    private lateinit var mEdtEmailLogin: EditText
    private lateinit var mEdtPassWordLogin: EditText
    private lateinit var mLoginViewModel: LoginViewModel
    private lateinit var mProgressDialog: ProgressDialog

    override fun getLayoutID() = R.layout.activity_login
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mBtnLogin = findViewById(R.id.mBtnLogin_2)
        mBtnSignUp = findViewById(R.id.mBtnSignUp_2)
        mEdtEmailLogin = findViewById(R.id.mEmailLogin)
        mEdtPassWordLogin = findViewById(R.id.mPassWordLogin)
        mLoginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        if (mLoginViewModel.checkCurrentUser()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        getContextThis(this)
        mBtnLogin.setOnClickListener {
            val email = mEdtEmailLogin.text.toString()
            val passWord = mEdtPassWordLogin.text.toString()
            if (checkNetwork()) {
                if (TextUtils.isEmpty(passWord) || TextUtils.isEmpty(email)) {
                    Toast.makeText(this, "name or pass word are required", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    auth.signInWithEmailAndPassword(email, passWord)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                //progress dialog
                                mProgressDialog = ProgressDialog(this)
                                mProgressDialog.show()
                                mProgressDialog.setContentView(R.layout.item_progressbar)
                                mProgressDialog.window?.setBackgroundDrawableResource(
                                    android.R.color.transparent
                                )
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Name Or Email Are Invalid ",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
            }
        }
        //check if user login then navigate to user screen
        mBtnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}