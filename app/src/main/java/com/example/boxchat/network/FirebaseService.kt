package com.example.boxchat.network

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.boxchat.R
import com.example.boxchat.ui.main.MainActivity
import com.example.boxchat.ui.main.chat.ChatActivity
import com.example.boxchat.ui.main.friends.ChatWithFriendFragment
import com.example.boxchat.ui.main.users.StrangerFragment
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseService : FirebaseMessagingService() {

    val CHANGNEL_ID = "my_notification_channel"

    companion object {
        var sharePref: SharedPreferences? = null
        var   token: String?
            get() {
                return sharePref?.getString("token", "")
            }
            set(value) {
                sharePref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }

    //handle messages received
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d("senderid", "onMessageReceived: ${p0.senderId}")

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //val notificationId = Random.nextInt()
        val notificationId = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotification(notificationManager)
        }

       // intent.addFlags
        val bitmap = BitmapFactory.decodeResource(resources,R.mipmap.ic_avatar)
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.music)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, CHANGNEL_ID)
            .setContentTitle(p0.data["title"])
            .setContentText(p0.data["message"])
            .setLargeIcon(bitmap)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(p0.data["message"]) )
            .setSound(uri)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    //Create a notification channel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(notificationManager: NotificationManager) {
        val channelName = "ChannelFirebaseChat"
        val channel = NotificationChannel(CHANGNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My Firebase Chat Description"
            enableLights(true)
            lightColor = Color.WHITE

        }
        notificationManager.createNotificationChannel(channel)
    }
}