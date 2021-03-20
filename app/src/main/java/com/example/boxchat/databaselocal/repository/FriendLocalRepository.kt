package com.example.boxchat.databaselocal.repository

import androidx.lifecycle.LiveData
import com.example.boxchat.databaselocal.dao.FriendLocalDao
import com.example.boxchat.databaselocal.entity.FriendLocal

class FriendLocalRepository(private val friendLocalDao: FriendLocalDao) {
    val readAllDataFromFriend : LiveData<List<FriendLocal>> = friendLocalDao.readAllDataFromFriend()

    suspend fun addFriend(friendLocal: FriendLocal) {
        friendLocalDao.addFriendLocal(friendLocal)
    }
}