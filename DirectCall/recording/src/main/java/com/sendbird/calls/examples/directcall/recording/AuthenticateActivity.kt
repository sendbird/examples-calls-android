package com.sendbird.calls.examples.directcall.recording

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.sendbird.calls.AuthenticateParams
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.User
import com.sendbird.calls.handler.AuthenticateHandler

class AuthenticateActivity : AppCompatActivity() {
    lateinit var applicationIdEt: EditText
    lateinit var userIdEt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)
        applicationIdEt = findViewById<EditText>(R.id.et_application_id).apply { setText(
            BaseApplication.APP_ID
        ) }
        userIdEt = findViewById(R.id.et_user_id)
        findViewById<Button>(R.id.btn_sign_in).setOnClickListener(this::onSignInButtonClicked)
        authenticateAutomatically()
    }

    private fun authenticateAutomatically() {
        SharedPreferencesManager.userId?.let {
            SendBirdCall.authenticate(AuthenticateParams(it), object : AuthenticateHandler {
                override fun onResult(user: User?, e: SendBirdException?) {
                    if (e == null) {
                        goToMainActivity()
                    }
                }
            })
        }
    }

    private fun onSignInButtonClicked(view: View) {
        val appId = applicationIdEt.text?.toString()
        val userId = userIdEt.text?.toString()

        if (appId.isNullOrEmpty()) {
            showToast("App ID is empty.")
            return
        }

        if (userId.isNullOrEmpty()) {
            showToast("User ID is empty.")
            return
        }

        SendBirdCall.init(applicationContext, appId)
        SendBirdCall.authenticate(AuthenticateParams(userId), object : AuthenticateHandler {
            override fun onResult(user: User?, e: SendBirdException?) {
                if (e != null) {
                    showToast(e.message ?: e.toString())
                    return
                }

                SharedPreferencesManager.userId = userId
                goToMainActivity()
            }
        })
    }

    private fun goToMainActivity() {
        FcmTokenManager.refreshToken()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}