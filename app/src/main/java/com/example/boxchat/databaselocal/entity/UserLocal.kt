package com.example.boxchat.databaselocal.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserLocal(
    @PrimaryKey
    val userId: String,
    val userName: String,
    val userProfileImage: String
)