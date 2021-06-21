package com.sendbird.calls.examples.directcall

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
        applicationIdEt = findViewById<EditText>(R.id.et_application_id).apply { setText(DEFAULT_APP_ID) }
        userIdEt = findViewById(R.id.et_user_id)
        findViewById<Button>(R.id.btn_sign_in).setOnClickListener(this::onSignInButtonClicked)
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

                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    putExtra(MainActivity.INTENT_EXTRA_ENTRY_FRAGMENT_TYPE, MainActivity.FragmentType.DIAL)
                }
                startActivity(intent)
                finish()
            }
        })
    }

    companion object {
        private const val DEFAULT_APP_ID = "773D12BD-8A51-4DB6-AF33-F59D189F006C"
    }
}