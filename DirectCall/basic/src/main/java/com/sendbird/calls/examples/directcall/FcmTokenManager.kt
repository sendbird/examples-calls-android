package com.sendbird.calls.examples.directcall

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.handler.CompletionHandler

object FcmTokenManager {

    fun setFcmToken(token: String): Boolean {
        val previousToken = SharedPreferencesManager.registeredToken
        if (token == previousToken) {
            return false
        }

        SharedPreferencesManager.token = token
        if (SendBirdCall.currentUser != null) {
            SendBirdCall.registerPushToken(token, false, object : CompletionHandler {
                override fun onResult(e: SendBirdException?) {
                    if (e == null) {
                        SharedPreferencesManager.registeredToken = token
                    } else {
                        Log.e("FcmTokenManager", "Failed to register push token. $e")
                    }
                }
            })
        }

        return true
    }

    fun refreshToken() {
        if (SendBirdCall.currentUser == null) {
            return
        }

        val token = SharedPreferencesManager.token
        val registeredToken = SharedPreferencesManager.registeredToken
        if (token != null && registeredToken != null && token == registeredToken) {
            return
        }

        if (token == null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }

                task.result?.let {
                    setFcmToken(it)
                }
            }
        } else if (token != registeredToken) {
            setFcmToken(token)
        }
    }
}