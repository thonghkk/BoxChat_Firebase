package com.example.boxchat.databaselocal.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_table")
data class FriendLocal(
    @PrimaryKey
    val userId: String="",
    val userName: String="",
    val userProfileImage: String=""
)