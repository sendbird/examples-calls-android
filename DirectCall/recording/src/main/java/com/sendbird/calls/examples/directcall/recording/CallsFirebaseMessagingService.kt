package com.sendbird.calls.examples.directcall.recording

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sendbird.calls.*
import com.sendbird.calls.handler.AuthenticateHandler
import com.sendbird.calls.handler.DirectCallListener
import com.sendbird.calls.handler.SendBirdCallListener

class CallsFirebaseMessagingService: FirebaseMessagingService() {
    init {
        SendBirdCall.addListener(TAG, object : SendBirdCallListener() {
            private val callListener = object : DirectCallListener() {
                override fun onConnected(call: DirectCall) {}

                override fun onEnded(call: DirectCall) {
                    val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(NOTIFICATION_ID)
                }
            }
            override fun onRinging(call: DirectCall) {
                val userId = SharedPreferencesManager.userId ?: return
                if (SendBirdCall.currentUser == null) {
                    SendBirdCall.authenticate(AuthenticateParams(userId), object : AuthenticateHandler {
                        override fun onResult(user: User?, e: SendBirdException?) {
                            showNotification(call)
                        }
                    })
                } else {
                    showNotification(call)
                }
                call.setListener(callListener)
            }
        })
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FcmTokenManager.setFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived(remoteMessage: ${remoteMessage.data})")
        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.data)) {
            Log.d(TAG, "handleFirebaseMessageData succeeded.")
        } else {
            Log.d(TAG, "handleFirebaseMessageData failed.")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SendBirdCalls"
            val descriptionText = "Notification for the incoming calls."

            val importance = IMPORTANCE_HIGH

            //define your own channel code here i used a predefined constant
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            // I am using application class's context here
            val notificationManager: NotificationManager =
                application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(call: DirectCall) {
        createNotificationChannel()
        val acceptIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.INTENT_EXTRA_CALL_ID, call.callId)
            putExtra(MainActivity.INTENT_EXTRA_IS_ACCEPTED, true)
        }

        val declineIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.INTENT_EXTRA_CALL_ID, call.callId)
            putExtra(MainActivity.INTENT_EXTRA_IS_DECLINED, true)
        }

        val randomRequestCode = (Int.MIN_VALUE..Int.MAX_VALUE).random()
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sendbird)
            .setContentTitle("Ringing")
            .setContentText("${call.remoteUser?.userId} is calling")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                NotificationCompat.Action(
                    0,
                    "Accept",
                    PendingIntent.getActivity(this, randomRequestCode, acceptIntent, 0)
                )
            )
            .addAction(
                NotificationCompat.Action(
                    0,
                    "Decline",
                    PendingIntent.getActivity(this,randomRequestCode + 1, declineIntent, 0)
                )
            )

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        private val TAG = CallsFirebaseMessagingService::class.java.simpleName
        private const val CHANNEL_ID = "SendbirdCalls Ringing"
        private const val NOTIFICATION_ID = 0
    }
}