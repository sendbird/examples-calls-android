package com.sendbird.calls.examples.directcall.recording

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val dangerousPermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = dangerousPermissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()
            requestPermissions(permissions, REQUEST_CODE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            Log.d("MainActivity", "[${permissions.joinToString()}] is granted.")
        }
    }

    companion object {
        const val INTENT_EXTRA_CALL_ID = "com.sendbird.calls.EXTRA_CALL_ID"
        const val INTENT_EXTRA_IS_ACCEPTED = "com.sendbird.calls.EXTRA_IS_ACCEPTED"
        const val INTENT_EXTRA_IS_DECLINED = "com.sendbird.calls.EXTRA_IS_DECLINED"
        const val REQUEST_CODE_PERMISSION = 0
    }
}
