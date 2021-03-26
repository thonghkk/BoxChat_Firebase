package com.example.boxchat.ui.signup

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.utils.CheckNetwork.Companion.getContextThis
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    private lateinit var btnSignUp: Button
    private lateinit var btnLogin: Button
    private lateinit var mEdtName: EditText
    private lateinit var mEdtEmail: EditText
    private lateinit var mEdtPassWord: EditText
    private lateinit var mEdtConfirmPassWord: EditText
    private lateinit var mSignUpViewModel: SignUpViewModel
    private lateinit var mProgressDialog: ProgressDialog

    override fun getLayoutID() = R.layout.activity_sign_up
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        btnSignUp = findViewById(R.id.mBtnSignUp)
        btnLogin = findViewById(R.id.mBtnLogin)
        mEdtName = findViewById(R.id.mNameSignUp)
        mEdtEmail = findViewById(R.id.mEmailSignUp)
        mEdtPassWord = findViewById(R.id.mPassWordSignUp)
        mEdtConfirmPassWord = findViewById(R.id.mConfirmPassWordSignUp)
        mSignUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        getContextThis(this)
        btnSignUp.setOnClickListener {
            val userName = mEdtName.text.toString()
            val email = mEdtEmail.text.toString()
            val password = mEdtPassWord.text.toString()
            val confirmPassWord = mEdtConfirmPassWord.text.toString()

            if (checkNetwork()) {
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(this, "User Name is require", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "PassWord is require", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(this, "Email is require", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(confirmPassWord)) {
                    Toast.makeText(this, "Confirm PassWord is require", Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassWord) {
                    Toast.makeText(this, "PassWord not match", Toast.LENGTH_SHORT).show()
                } else {
                    registerUser(userName, email, password)
                    //dialog
                    mProgressDialog = ProgressDialog(this)
                    mProgressDialog.show()
                    mProgressDialog.setContentView(R.layout.item_progressbar)
                    mProgressDialog.window?.setBackgroundDrawableResource(
                        android.R.color.transparent
                    )
                }
            } else {
                Toast.makeText(this, "Connect Internet", Toast.LENGTH_SHORT).show()
            }

        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val hashMap: HashMap<String, String> = HashMap()
                    val userCurr: FirebaseUser? = auth.currentUser
                    val userId: String = userCurr!!.uid
                    hashMap["userId"] = userId
                    hashMap["userName"] = userName
                    hashMap["email"] = email
                    hashMap["userProfileImage"] = ""
                    mSignUpViewModel.databaseReference.child(userId).setValue(hashMap)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Not Connect ", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }
}