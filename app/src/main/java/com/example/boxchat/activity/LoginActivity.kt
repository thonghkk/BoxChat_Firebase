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

class LoginActivity : AppCompatActivity() {

    private lateinit var mBtnLogin: Button
    private lateinit var mBtnSignUp: Button
    private lateinit var mEdtEmailLogin: EditText
    private lateinit var mEdtPassWordLogin: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mBtnLogin = findViewById(R.id.mBtnLogin_2)
        mBtnSignUp = findViewById(R.id.mBtnSignUp_2)
        mEdtEmailLogin = findViewById(R.id.mEmailLogin)
        mEdtPassWordLogin = findViewById(R.id.mPassWordLogin)

        auth = FirebaseAuth.getInstance()
//        firebaseUser = auth.currentUser!!

        if(auth.currentUser != null){
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }

        //check if user login then navigate to user screen
        mBtnLogin.setOnClickListener {
            val email = mEdtEmailLogin.text.toString()
            val passWord = mEdtPassWordLogin.text.toString()

            if (TextUtils.isEmpty(passWord) || TextUtils.isEmpty(email)) {
                Toast.makeText(this, "name or pass word are required", Toast.LENGTH_SHORT).show()

            } else {
                auth.signInWithEmailAndPassword(email, passWord)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            val intent = Intent(this, UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Name Or Email Are Invalid", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
        mBtnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}