package com.example.boxchat.network

import android.app.*
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.example.boxchat.R
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.MainActivity
import com.example.boxchat.ui.main.chat.ChatActivity
import com.example.boxchat.ui.main.friends.ChatWithFriendFragment
import com.example.boxchat.ui.main.friends.ChatWithFriendViewModel
import com.example.boxchat.ui.main.users.StrangerFragment
import com.example.boxchat.ui.main.users.ViewStrangerActivity
import com.example.boxchat.utils.CheckNetwork
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseService : FirebaseMessagingService() {

    val CHANGNEL_ID = "my_notification_channel"

    companion object {
        var sharePref: SharedPreferences? = null
        var token: String?
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
        Log.d("a", "onMessageReceived: ${p0.senderId}")
        val userId = p0.data["userId"]
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("userId", userId)
        }
        val resultPendingIntent: PendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //val notificationId = Random.nextInt()
        val notificationId = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotification(notificationManager)
        }

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_avatar)
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.music)


        //notification 1
        val notification = NotificationCompat.Builder(this, CHANGNEL_ID)
            .setContentTitle(p0.data["title"])
            .setContentText(p0.data["message"])
            .setLargeIcon(bitmap)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(p0.data["message"]))
            .setSound(uri)
            .setPriority(IMPORTANCE_HIGH)
            .build()
        notificationManager.notify(notificationId, notification)

    }

    //Create a notification channel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(notificationManager: NotificationManager) {
        val channelName = "ChannelFirebaseChat"
        val channelName2 = "ChannelFirebaseMakeFriend"
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.music)
        val attribute: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(CHANGNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My Firebase Chat Description"
            enableLights(true)
            lightColor = Color.WHITE
            setSound(uri, attribute)
        }
        notificationManager.createNotificationChannel(channel)
    }
}