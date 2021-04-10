package com.example.boxchat.network

import com.example.boxchat.commom.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        var notificationApi: NotificationApi

        init {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            notificationApi = retrofit.create(NotificationApi::class.java)
        }
    }
}