package com.example.boxchat.databaselocal.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yourself_table")
data class YourselfLocal (
    @PrimaryKey
    var userId: String = "",
    val userName: String = "",
    var userProfileImage: String = ""
)

