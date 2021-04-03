package com.example.boxchat.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.boxchat.commom.Firebase

abstract class BaseFragment : Fragment() {
    abstract fun getLayoutID(): Int
    abstract fun onViewReady(view: View)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(getLayoutID(), container, false)
        onViewReady(v)
        return v
    }

    fun status(status: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["statusExist"] = status
        Firebase.firebaseDatabase.getReference("Users").child(Firebase.auth.uid!!)
            .updateChildren(hashMap as Map<String, Any>)
    }

    override fun onStart() {
        super.onStart()
        status("online")
    }
}