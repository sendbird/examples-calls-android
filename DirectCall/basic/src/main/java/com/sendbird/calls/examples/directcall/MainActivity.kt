package com.sendbird.calls.examples.directcall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    lateinit var entryFragmentType: FragmentType
    private val dialFragment = DialFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        entryFragmentType = intent.extras?.getSerializable(INTENT_EXTRA_ENTRY_FRAGMENT_TYPE) as? FragmentType ?: FragmentType.DIAL
        transactFragment(entryFragmentType)
    }

    private fun transactFragment(type: FragmentType) {
        val fragment = when (type) {
            FragmentType.DIAL -> dialFragment
            FragmentType.CALLING -> CallingFragment()
        }

        supportFragmentManager.beginTransaction().add(R.id.main_frame_layout, fragment).commit()
    }

    enum class FragmentType {
        DIAL, CALLING
    }

    companion object {
        const val INTENT_EXTRA_ENTRY_FRAGMENT_TYPE = "com.sendbird.calls.EXTRA_ENTRY_FRAGMENT_TYPE"
    }
}
