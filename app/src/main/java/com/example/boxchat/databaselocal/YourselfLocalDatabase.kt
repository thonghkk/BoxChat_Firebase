package com.example.boxchat.databaselocal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.boxchat.databaselocal.dao.UserLocalDao
import com.example.boxchat.databaselocal.dao.YourselfLocalDao
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.databaselocal.entity.YourselfLocal

@Database(entities = [YourselfLocal::class], version = 1, exportSchema = false)
abstract class YourselfLocalDatabase : RoomDatabase() {
    abstract fun yourselfLocalDao(): YourselfLocalDao

    companion object {
        @Volatile
        private var INSTANCE: YourselfLocalDatabase? = null
        fun getDatabaseFromMe(context: Context): YourselfLocalDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YourselfLocalDatabase::class.java,
                    "yourself_table"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}



