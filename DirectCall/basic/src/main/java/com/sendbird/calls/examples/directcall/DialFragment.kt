package com.sendbird.calls.examples.directcall

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.sendbird.calls.DialParams
import com.sendbird.calls.DirectCall
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.handler.DialHandler
import com.sendbird.calls.handler.SendBirdCallListener

class DialFragment : Fragment() {
    lateinit var userIdEditText: EditText
    lateinit var radioGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.dial_button_dial).setOnClickListener(this::onDialButtonClicked)
        userIdEditText = view.findViewById(R.id.dial_edit_text_user_id)
        radioGroup = view.findViewById(R.id.dial_radio_group_call_type)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
        SendBirdCall.addListener(TAG, sendbirdCallListener)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
        SendBirdCall.removeListener(TAG)
    }

    private fun onDialButtonClicked(view: View) {
        val userId = userIdEditText.text?.toString()
        if (userId.isNullOrEmpty()) {
            context?.showToast("user ID is empty")
            return
        }
        val isVideoCall = radioGroup.checkedRadioButtonId == R.id.dial_radio_button_video
        SendBirdCall.dial(DialParams(userId).setVideoCall(isVideoCall), object : DialHandler {
            override fun onResult(call: DirectCall?, e: SendBirdException?) {
                call?.callId?.let {
                    val action = DialFragmentDirections.actionDialFragmentToCallingFragment(it)
                    findNavController().navigate(action)
                }
                e?.let { context?.showToast(it.message ?: it.toString()) }
            }
        })
    }

    private val sendbirdCallListener = object : SendBirdCallListener() {
        override fun onRinging(call: DirectCall) {
            Log.d(TAG, "onRinging() called with: call = $call")
            call.callId?.let {
                val action = DialFragmentDirections.actionDialFragmentToCallingFragment(it)
                findNavController().navigate(action)
            }
        }
    }

    companion object {
        val TAG = DialFragment::class.java.simpleName
    }
}