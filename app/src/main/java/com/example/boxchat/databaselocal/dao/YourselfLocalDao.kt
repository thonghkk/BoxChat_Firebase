package com.example.boxchat.databaselocal.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.boxchat.databaselocal.entity.YourselfLocal

@Dao
interface YourselfLocalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addYourselfLocal(yourselfLocal: YourselfLocal)

    @Query("SELECT * FROM yourself_table")
    fun readAllDataFromYourself(): LiveData<List<YourselfLocal>>

    @Query("DELETE FROM yourself_table ")
    suspend fun deleteAllYourSelf()
}


