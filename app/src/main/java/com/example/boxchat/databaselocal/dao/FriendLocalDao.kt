package com.example.boxchat.databaselocal.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.boxchat.databaselocal.entity.FriendLocal

@Dao
interface FriendLocalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFriendLocal(friendLocal: FriendLocal)

    @Query("SELECT * FROM friend_table")
    fun readAllDataFromFriend(): LiveData<List<FriendLocal>>

}