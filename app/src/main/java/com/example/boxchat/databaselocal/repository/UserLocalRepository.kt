package com.example.boxchat.databaselocal.repository

import androidx.lifecycle.LiveData
import com.example.boxchat.databaselocal.dao.UserLocalDao
import com.example.boxchat.databaselocal.entity.UserLocal

class UserLocalRepository(private val userLocalDao: UserLocalDao) {
    val readAllData: LiveData<List<UserLocal>> = userLocalDao.readAllData()

    suspend fun addUser(userLocal: UserLocal) {
        userLocalDao.addUserLocal(userLocal)
    }
}