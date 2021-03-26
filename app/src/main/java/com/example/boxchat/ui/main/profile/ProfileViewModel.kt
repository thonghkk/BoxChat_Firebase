package com.example.boxchat.ui.main.profile

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.boxchat.utils.CheckNetwork.Companion.context
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.databaselocal.YourselfLocalDatabase
import com.example.boxchat.databaselocal.entity.YourselfLocal
import com.example.boxchat.databaselocal.repository.YourselfLocalRepository
import com.example.boxchat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    var databaseReferenceProfile = getUserId()
    val me = MutableLiveData<List<User>>()
    private var yourSelfList = mutableListOf<User>()

    //get info Yourself in Drive
    val readAllDataFromMe: LiveData<List<YourselfLocal>>
    private val repository: YourselfLocalRepository

    init {
        val yourselfLocalDao =
            YourselfLocalDatabase.getDatabaseFromMe(application).yourselfLocalDao()
        repository = YourselfLocalRepository(yourselfLocalDao)
        readAllDataFromMe = repository.readAllDataFromMe
        getAddMeLocal()
        getProfile()
    }


    fun addProfile(yourselfLocal: YourselfLocal) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addYourself(yourselfLocal)
        }
    }

    private fun getUserId(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users")
    }

    private fun getAddMeLocal() {
        databaseReferenceProfile.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mYourself = dataSnapShot.getValue(YourselfLocal::class.java)
                    if (mYourself?.userId == user?.uid){
                        addProfile(mYourself!!)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun addMe(yourSelf: List<User>) {
        me.value = yourSelf
    }

    private fun getProfile() {
        databaseReferenceProfile.child(auth.uid!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                yourSelfList.add(user!!)
                addMe(yourSelfList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}