package com.example.boxchat.databaselocal.repository

import androidx.lifecycle.LiveData
import com.example.boxchat.databaselocal.dao.YourselfLocalDao
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.databaselocal.entity.YourselfLocal

class YourselfLocalRepository(private val yourselfLocalDao: YourselfLocalDao) {

    val readAllDataFromMe: LiveData<List<YourselfLocal>> = yourselfLocalDao.readAllDataFromYourself()

    suspend fun addYourself(yourselfLocal: YourselfLocal) {
        yourselfLocalDao.addYourselfLocal(yourselfLocal)
    }

}