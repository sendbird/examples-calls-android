package com.sendbird.calls.examples.directcall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    lateinit var entryFragmentType: FragmentType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        entryFragmentType = intent.extras?.getSerializable(INTENT_EXTRA_ENTRY_FRAGMENT_TYPE) as? FragmentType ?: FragmentType.DIAL
    }
    enum class FragmentType {
        DIAL, CALLING
    }

    companion object {
        const val INTENT_EXTRA_ENTRY_FRAGMENT_TYPE = "com.sendbird.calls.EXTRA_ENTRY_FRAGMENT_TYPE"
    }
}
