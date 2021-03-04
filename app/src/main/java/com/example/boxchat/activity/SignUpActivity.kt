package com.example.boxchat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.boxchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var btnSignUp: Button
    private lateinit var btnLogin: Button
    private lateinit var mEdtName: EditText
    private lateinit var mEdtEmail: EditText
    private lateinit var mEdtPassWord: EditText
    private lateinit var mEdtConfirmPassWord: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUp = findViewById(R.id.mBtnSignUp)
        btnLogin = findViewById(R.id.mBtnLogin)

        mEdtName = findViewById(R.id.mNameSignUp)
        mEdtEmail = findViewById(R.id.mEmailSignUp)
        mEdtPassWord = findViewById(R.id.mPassWordSignUp)
        mEdtConfirmPassWord = findViewById(R.id.mConfirmPassWordSignUp)

        auth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener {
            val userName = mEdtName.text.toString()
            val email = mEdtEmail.text.toString()
            val password = mEdtPassWord.text.toString()
            val confirmPassWord = mEdtConfirmPassWord.text.toString()

            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(this, "User Name is require", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "PassWord is require", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email is require", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(confirmPassWord)) {
                Toast.makeText(this, "Confirm PassWord is require", Toast.LENGTH_SHORT).show()
            }
            if (password != confirmPassWord) {
                Toast.makeText(this, "PassWord not match", Toast.LENGTH_SHORT).show()
            }

            registerUser(userName, email, password)
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
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user?.uid!!

                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId)
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["userName"] = userName
                    hashMap["email"] = email
                    hashMap["userProfileImage"] = ""

//                    user.sendEmailVerification()
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                Log.d("Send Email", "Email sent")
//                            }
//                        }

                    databaseReference.setValue(hashMap).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            //auto open home activity
                            mEdtName.setText("")
                            mEdtEmail.setText("")
                            mEdtPassWord.setText("")
                            mEdtConfirmPassWord.setText("")

                            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
    }
}