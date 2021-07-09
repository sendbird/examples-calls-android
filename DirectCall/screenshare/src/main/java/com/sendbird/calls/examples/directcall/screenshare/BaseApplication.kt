package com.sendbird.calls.examples.directcall.screenshare

import android.app.Application
import com.sendbird.calls.SendBirdCall

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(applicationContext)
        SendBirdCall.init(applicationContext, APP_ID)
        SendBirdCall.setLoggerLevel(SendBirdCall.LOGGER_INFO)
    }

    companion object {
        const val APP_ID = "35F54875-BF9D-433D-8BD6-4923B035D649"
    }
}