package com.example.boxchat.databaselocal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.boxchat.databaselocal.dao.FriendLocalDao
import com.example.boxchat.databaselocal.dao.UserLocalDao
import com.example.boxchat.databaselocal.entity.FriendLocal
import com.example.boxchat.databaselocal.entity.UserLocal


@Database(entities = [FriendLocal::class], version = 1, exportSchema = false)
abstract class FriendLocalDatabase : RoomDatabase() {
    abstract fun friendLocalDao(): FriendLocalDao

    companion object {
        @Volatile
        private var INSTANCE: FriendLocalDatabase? = null

        fun getFriendFromDatabase(context: Context): FriendLocalDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FriendLocalDatabase::class.java,
                    "friend_table"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}