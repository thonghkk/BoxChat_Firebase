package com.example.boxchat.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

const val RC_SIGN_IN = 120

class LoginActivity : BaseActivity() {

    private lateinit var mBtnLogin: Button
    private lateinit var mBtnSignUp: Button
    private lateinit var btnLoginWithGoogle: Button
    private lateinit var mEdtEmailLogin: EditText
    private lateinit var mEdtPassWordLogin: EditText
    private lateinit var mLoginViewModel: LoginViewModel
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun getLayoutID() = R.layout.activity_login
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mBtnLogin = findViewById(R.id.mBtnLogin_2)
        mBtnSignUp = findViewById(R.id.mBtnSignUp_2)
        mEdtEmailLogin = findViewById(R.id.mEmailLogin)
        mEdtPassWordLogin = findViewById(R.id.mPassWordLogin)
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle)

        mLoginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        //login with Email
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

        //Login With Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnLoginWithGoogle.setOnClickListener {

            mProgressDialog = ProgressDialog(this)
            mProgressDialog.show()
            mProgressDialog.setContentView(R.layout.item_progressbar)
            mProgressDialog.window?.setBackgroundDrawableResource(
                android.R.color.transparent
            )
            signIn()
        }
    }

    //signIn With Google
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("Sign With Google", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("Sign With Google", "Google sign in failed", e)
                }
            } else {
                Log.d("Exception", "onActivityResult: ${exception?.message} ")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    startActivity(Intent(this, LoginActivity::class.java))
                    pushData()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun pushData() {
        val hashMap: HashMap<String, String> = HashMap()
        val userCurr: FirebaseUser? = auth.currentUser
        val userId: String = userCurr!!.uid
        hashMap["userId"] = userId
        hashMap["userName"] = userCurr.displayName
        hashMap["email"] = userCurr.email
        hashMap["userProfileImage"] = ""
        hashMap["userHomeTown"] = ""
        hashMap["userBirthDay"] = ""
        hashMap["userEnglishCertificate"] = ""
        mLoginViewModel.databaseReference.child(userId).setValue(hashMap)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    mProgressDialog = ProgressDialog(this)
                    mProgressDialog.show()
                    mProgressDialog.setContentView(R.layout.item_progressbar)
                    mProgressDialog.window?.setBackgroundDrawableResource(
                        android.R.color.transparent
                    )
                    finish()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Not Connect ", Toast.LENGTH_SHORT).show()
                }
            }
    }
}