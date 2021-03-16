package com.example.boxchat.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.model.Chat
import com.example.boxchat.ui.login.LoginActivity
import com.example.boxchat.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : BaseActivity() {

    private lateinit var btnSignUp: Button
    private lateinit var btnLogin: Button
    private lateinit var mEdtName: EditText
    private lateinit var mEdtEmail: EditText
    private lateinit var mEdtPassWord: EditText
    private lateinit var mEdtConfirmPassWord: EditText
    private lateinit var mSignUpViewModel: SignUpViewModel

    override fun getLayoutID() = R.layout.activity_sign_up
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        btnSignUp = findViewById(R.id.mBtnSignUp)
        btnLogin = findViewById(R.id.mBtnLogin)
        mEdtName = findViewById(R.id.mNameSignUp)
        mEdtEmail = findViewById(R.id.mEmailSignUp)
        mEdtPassWord = findViewById(R.id.mPassWordSignUp)
        mEdtConfirmPassWord = findViewById(R.id.mConfirmPassWordSignUp)
        mSignUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        btnSignUp.setOnClickListener {
            val userName = mEdtName.text.toString()
            val email = mEdtEmail.text.toString()
            val password = mEdtPassWord.text.toString()
            val confirmPassWord = mEdtConfirmPassWord.text.toString()

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
                    val userCurr:FirebaseUser? = auth.currentUser
                    val userId:String =  userCurr!!.uid
                    hashMap["userId"] =  userId
                    hashMap["userName"] = userName
                    hashMap["email"] = email
                    hashMap["userProfileImage"] = ""

//                    user.sendEmailVerification()
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                Log.d("Send Email", "Email sent")
//                            }
//                        }

                    mSignUpViewModel.databaseReference.child(userId).setValue(hashMap)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
            }
    }
}