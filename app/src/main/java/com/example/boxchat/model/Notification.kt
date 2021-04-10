package com.example.boxchat.model

data class Notification(
    val title: String,
    val message: String,
    var userId: String = "",
    var channel: String
)