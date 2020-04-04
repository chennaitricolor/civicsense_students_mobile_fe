package com.gcc.smartcity.pushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gcc.smartcity.R
import com.gcc.smartcity.loginandregistration.LoginActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        token.let {
            Log.d("PUSH NOTIFICATION TOKEN", "Refreshed token: $it")
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.run {
            val hashMap = HashMap<String, Any>()
            hashMap.putAll(message.data)
            val id = System.currentTimeMillis()
            sendNotification(
                data["title"] ?: "error", data["body"]
                    ?: "error", hashMap, id.toInt()
            )
        }
        Log.d("PUSH NOTIFICATION MSG", "From: ${message.from}")
        Log.d("PUSH NOTIFICATION MSG", "From: ${message.from}")
    }

    private fun sendNotification(
        messageTitle: String,
        messageBody: String,
        data: HashMap<String, Any>,
        notificationId: Int
    ) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val bundle = Bundle()
        bundle.putSerializable("hashmap", data)
        intent.putExtras(bundle)
        val pendingIntent = PendingIntent.getActivity(
            this, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "10000"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val icon = BitmapFactory.decodeResource(this.resources, R.drawable.img_avatar)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.img_avatar)
            .setColor(resources.getColor(R.color.colorPrimary))
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent).setGroup(notificationId.toString())

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable messageTitle",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}