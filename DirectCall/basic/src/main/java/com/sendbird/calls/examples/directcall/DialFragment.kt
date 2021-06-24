package com.sendbird.calls.examples.directcall

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.sendbird.calls.DialParams
import com.sendbird.calls.DirectCall
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.handler.CompletionHandler
import com.sendbird.calls.handler.DialHandler

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
        view.findViewById<Button>(R.id.dial_button_sign_out).setOnClickListener(this::onSignOutButtonClicked)
        userIdEditText = view.findViewById(R.id.dial_edit_text_user_id)
        radioGroup = view.findViewById(R.id.dial_radio_group_call_type)
        val callId = activity?.intent?.extras?.getString(MainActivity.INTENT_EXTRA_CALL_ID)
        val isAccepted = activity?.intent?.extras?.getBoolean(MainActivity.INTENT_EXTRA_IS_ACCEPTED, false) ?: false
        val isDeclined = activity?.intent?.extras?.getBoolean(MainActivity.INTENT_EXTRA_IS_DECLINED, false) ?: false


        if (callId != null) {
            SendBirdCall.getCall(callId)?.let {
                if (it.isOngoing) {
                    navigateToCallFragment(callId, isAccepted, isDeclined)
                }
            }
        }
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
                call?.callId?.let { navigateToCallFragment(it) }
                e?.let { context?.showToast(it.message ?: it.toString()) }
            }
        })
    }

    private fun onSignOutButtonClicked(view: View) {
        if (SendBirdCall.currentUser == null) {
            activity?.showToast("Not authenticated yet.")
            return
        }

        val token = SharedPreferencesManager.token
        SharedPreferencesManager.token = null
        SharedPreferencesManager.userId = null
        SharedPreferencesManager.registeredToken = null

        val signOut = {
            SendBirdCall.deauthenticate(object : CompletionHandler {
                override fun onResult(e: SendBirdException?) {
                    val intent = Intent(context, AuthenticateActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            })
        }

        if (token != null) {
            SendBirdCall.unregisterPushToken(token, object : CompletionHandler {
                override fun onResult(e: SendBirdException?) {
                    signOut.invoke()
                }
            })
        } else {
            signOut.invoke()
        }
    }

    private fun navigateToCallFragment(
        callId: String,
        isAccepted: Boolean = false,
        isDeclined: Boolean = false
    ) {
        val action = DialFragmentDirections.actionDialFragmentToCallFragment(callId, isAccepted, isDeclined)
        findNavController().navigate(action)
    }

    companion object {
        val TAG = DialFragment::class.java.simpleName
    }
}