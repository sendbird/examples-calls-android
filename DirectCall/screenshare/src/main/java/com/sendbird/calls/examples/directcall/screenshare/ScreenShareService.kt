package com.sendbird.calls.examples.directcall.screenshare

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log


class ScreenShareService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("nathan", "onStartCommand: ")
        createNotificationChannel()

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }

        val notification: Notification = builder
            .setContentTitle("Sendbird Calls")
            .setContentText("Screen sharing...")
            .setSmallIcon(R.drawable.ic_sendbird)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Sendbird Calls ScreenShare Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "ScreenShareChannel"
        const val NOTIFICATION_ID: Int = 1
    }
}