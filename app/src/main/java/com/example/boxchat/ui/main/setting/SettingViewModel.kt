package com.example.boxchat.ui.main.setting

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.databaselocal.FriendLocalDatabase
import com.example.boxchat.databaselocal.UserLocalDatabase
import com.example.boxchat.databaselocal.YourselfLocalDatabase
import com.example.boxchat.databaselocal.entity.FriendLocal
import com.example.boxchat.databaselocal.entity.YourselfLocal
import com.example.boxchat.databaselocal.repository.FriendLocalRepository
import com.example.boxchat.databaselocal.repository.UserLocalRepository
import com.example.boxchat.databaselocal.repository.YourselfLocalRepository
import com.example.boxchat.model.User
import com.example.boxchat.utils.CheckNetwork
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(application: Application) : AndroidViewModel(application) {

    var databaseReferenceProfile = getUserId()
    val adminRef = getAdminList()
    val me = MutableLiveData<List<User>>()
    val userList = MutableLiveData<List<User>>()
    val adminList = MutableLiveData<List<User>>()
    val readAllDataFromMe: LiveData<List<YourselfLocal>>
    private var yourSelfList = mutableListOf<User>()
    private var listUser = mutableListOf<User>()
    private var listAdmin = mutableListOf<User>()
    private val repository: FriendLocalRepository
    private val repositoryUser: UserLocalRepository
    private val repositoryYourSelf: YourselfLocalRepository

    init {
        //Delete Local
        val friendLocalDao = FriendLocalDatabase.getFriendFromDatabase(application).friendLocalDao()
        repository = FriendLocalRepository(friendLocalDao)
        val userLocalDao = UserLocalDatabase.getDatabase(application).userLocalDao()
        repositoryUser = UserLocalRepository(userLocalDao)
        val youSelfLocalDao =
            YourselfLocalDatabase.getDatabaseFromMe(application).yourselfLocalDao()
        repositoryYourSelf = YourselfLocalRepository(youSelfLocalDao)
        readAllDataFromMe = repositoryYourSelf.readAllDataFromMe
        //get info of me
        getProfile()
        getNumberUser()
        getAdmin()
    }

    private fun getUserId(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Users")
    }

    private fun getAdminList(): DatabaseReference {
        return Firebase.firebaseDatabase.getReference("Admin").child(auth.uid!!)
    }

    fun addMe(yourSelf: List<User>) {
        me.value = yourSelf
    }

    fun addUser(user: List<User>) {
        userList.value = user
    }

    fun addAdmin(admin: List<User>) {
        adminList.value = admin
    }


    private fun getProfile() {
        databaseReferenceProfile.child(Firebase.auth.uid!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                yourSelfList.add(user!!)
                addMe(yourSelfList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(CheckNetwork.context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getNumberUser() {
        databaseReferenceProfile.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listUser.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    listUser.add(user!!)
                }
                addUser(listUser)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(CheckNetwork.context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getAdmin() {
        adminRef.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val admin = snapshot.getValue(User::class.java)
                if (admin != null) {
                    listAdmin.add(admin)
                    addAdmin(listAdmin)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun deleteAllFriendLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllFriend()
        }
    }

    fun deleteAllUserLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryUser.deleteAllUser()
        }
    }

    fun deleteAllYourself() {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryYourSelf.deleteAllYourself()
        }
    }
}