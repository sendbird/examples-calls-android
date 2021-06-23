package com.sendbird.calls.examples.directcall

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private lateinit var pref: SharedPreferences
    private const val PREFS_NAME = "SendBirdCallsPrefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_REGISTERED_TOKEN = "registered_token"
    private const val KEY_USER_ID = "user_id"

    var userId: String?
        get() = pref.getString(KEY_USER_ID, null)
        set(value) = pref.edit().putString(KEY_USER_ID, value).apply()

    var token: String?
        get() = pref.getString(KEY_TOKEN, null)
        set(value) = pref.edit().putString(KEY_TOKEN, value).apply()

    var registeredToken: String?
        get() = pref.getString(KEY_REGISTERED_TOKEN, null)
        set(value) = pref.edit().putString(KEY_REGISTERED_TOKEN, value).apply()

    fun init(context: Context) {
        pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}