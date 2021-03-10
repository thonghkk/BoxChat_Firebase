package com.example.boxchat.ui.login

import androidx.lifecycle.ViewModel
import com.example.boxchat.commom.Firebase.Companion.auth

class LoginViewModel:ViewModel() {
    fun checkCurrentUser():Boolean{
        return auth.currentUser != null
    }
}