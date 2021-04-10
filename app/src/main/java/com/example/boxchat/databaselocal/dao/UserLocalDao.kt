package com.example.boxchat.databaselocal.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.boxchat.databaselocal.entity.FriendLocal
import com.example.boxchat.databaselocal.entity.UserLocal

@Dao
interface UserLocalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUserLocal(userLocal: UserLocal)

    @Query("SELECT * FROM user_table")
    fun readAllDataFromUser(): LiveData<List<UserLocal>>

    @Query("DELETE FROM user_table ")
    suspend fun deleteAllUser()
}