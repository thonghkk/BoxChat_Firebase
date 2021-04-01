package com.example.boxchat.databaselocal.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.boxchat.databaselocal.entity.FriendLocal

@Dao
interface FriendLocalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFriendLocal(friendLocal: FriendLocal)

    @Query("SELECT * FROM friend_table")
    fun readAllDataFromFriend(): LiveData<List<FriendLocal>>

    @Delete
    suspend fun deleteFriendLocal(friendLocal: FriendLocal)

    @Query("DELETE FROM friend_table ")
    suspend fun deleteAllFriend()

}