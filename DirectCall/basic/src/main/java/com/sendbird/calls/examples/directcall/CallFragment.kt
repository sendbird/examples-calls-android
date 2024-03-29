package com.sendbird.calls.examples.directcall

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sendbird.calls.*
import com.sendbird.calls.handler.DirectCallListener
import org.webrtc.RendererCommon
import java.util.*

class CallFragment : Fragment() {
    private val args: CallFragmentArgs by navArgs()
    lateinit var directCall: DirectCall
    lateinit var statusTextView: TextView
    lateinit var callTypeTextView: TextView
    lateinit var myRoleTextView: TextView
    lateinit var remoteUserIdTextView: TextView
    lateinit var durationTextView: TextView
    lateinit var endButton: Button
    lateinit var localVideoView: SendBirdVideoView
    lateinit var remoteVideoView: SendBirdVideoView

    enum class CallStatus {
        DIALING, RINGING, CONNECTING, CONNECTED, RECONNECTING, ENDED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callId = args.callId
        val call = SendBirdCall.getCall(callId)
        if (call == null) {
            context?.showToast("Call is null.")
            findNavController().navigateUp()
            return
        }

        directCall = call.apply { setListener(directCallListener) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (directCall == null) {
            return
        }

        statusTextView = view.findViewById(R.id.call_textview_status)
        callTypeTextView = view.findViewById(R.id.call_textview_call_type)
        myRoleTextView = view.findViewById(R.id.call_textview_my_role)
        remoteUserIdTextView = view.findViewById(R.id.call_textview_remote_user_id)
        endButton = view.findViewById(R.id.call_button_end)
        localVideoView = view.findViewById(R.id.call_local_video_view)
        remoteVideoView = view.findViewById(R.id.call_remote_video_view)
        remoteVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        durationTextView = view.findViewById(R.id.call_textview_duration)

        statusTextView.text = if (directCall.myRole == DirectCallUserRole.CALLER ) {
            CallStatus.DIALING.toString()
        } else {
            CallStatus.RINGING.toString()
        }

        callTypeTextView.text = if (directCall.isVideoCall) {
            "video"
        } else {
            "audio"
        }

        myRoleTextView.text = directCall.myRole?.toString()
        remoteUserIdTextView.text = directCall.remoteUser?.userId
        endButton.setOnClickListener(this::onEndButtonClicked)
        if (!directCall.isVideoCall) {
            localVideoView.visibility = View.INVISIBLE
            remoteVideoView.visibility = View.INVISIBLE
        } else {
            directCall.setLocalVideoView(localVideoView)
            directCall.setRemoteVideoView(remoteVideoView)
        }

        if (args.isAccepted) {
            directCall.accept(
                AcceptParams()
                    .setCallOptions(
                        CallOptions()
                            .setRemoteVideoView(remoteVideoView)
                            .setLocalVideoView(localVideoView)
                    )
            )
        } else if (args.isDeclined) {
            directCall.end()
        }
    }

    private fun onEndButtonClicked(view: View) {
        directCall.end()
    }

    private val directCallListener: DirectCallListener = object : DirectCallListener() {
        var durationTimer: Timer? = null
        override fun onEstablished(call: DirectCall) {
            statusTextView.text = CallStatus.CONNECTING.toString()
            durationTimer = Timer()
            durationTimer?.schedule(object : TimerTask() {
                override fun run() {
                    durationTextView.text = "${directCall.duration.div(1000)}s"
                }
            }, 1000, 1000)
        }

        override fun onReconnected(call: DirectCall) {
            statusTextView.text = CallStatus.CONNECTED.toString()
        }

        override fun onReconnecting(call: DirectCall) {
            statusTextView.text = CallStatus.RECONNECTING.toString()
        }

        override fun onConnected(call: DirectCall) {
            statusTextView.text = CallStatus.CONNECTED.toString()
        }

        override fun onEnded(call: DirectCall) {
            statusTextView.text = CallStatus.ENDED.toString()
            durationTimer?.cancel()
            durationTimer = null
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigateUp()
            }, 1000)
        }
    }
}