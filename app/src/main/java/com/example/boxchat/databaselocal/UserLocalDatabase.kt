package com.example.boxchat.databaselocal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.boxchat.databaselocal.dao.UserLocalDao
import com.example.boxchat.databaselocal.entity.UserLocal


@Database(entities = [UserLocal::class],version = 1,exportSchema = false)
abstract class UserLocalDatabase:RoomDatabase() {

    abstract fun userLocalDao():UserLocalDao

    companion object{
        @Volatile
        private var INSTANCE: UserLocalDatabase? = null

        fun getDatabase(context: Context):UserLocalDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserLocalDatabase::class.java,
                    "user_table"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }

}