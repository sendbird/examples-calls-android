package com.sendbird.calls.examples.directcall.recording

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sendbird.calls.*
import com.sendbird.calls.RecordingOptions.RecordingType
import com.sendbird.calls.RecordingOptions.RecordingType.LOCAL_AUDIO_REMOTE_AUDIO_AND_VIDEO
import com.sendbird.calls.SendBirdCall.addRecordingListener
import com.sendbird.calls.handler.DirectCallListener
import com.sendbird.calls.handler.RecordingListener
import com.sendbird.calls.handler.RecordingStartedHandler
import org.webrtc.RendererCommon
import java.io.File
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
    lateinit var recordingButton: Button
    lateinit var localVideoView: SendBirdVideoView
    lateinit var remoteVideoView: SendBirdVideoView

    private var currentlyRecording: Boolean = false
    private var recordingId: String = ""
    private val recordingTypes = RecordingType.values()

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
        currentlyRecording = false
        recordingId = ""

        addRecordingListener(CallFragment::class.toString(), object : RecordingListener {
            override fun onRecordingSucceeded(
                call: DirectCall,
                recordingId: String,
                recordingOptions: RecordingOptions,
                outputFilePath: String
            ) {
                showToast("Recording(id=$recordingId) succeeded, saved to $outputFilePath")
            }

            override fun onRecordingFailed(
                call: DirectCall,
                recordingId: String,
                e: SendBirdException
            ) {
                showToast("Recording(id=$recordingId) failed due to error $e")
            }
        })
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
        recordingButton = view.findViewById(R.id.call_button_recording)
        localVideoView = view.findViewById(R.id.call_local_video_view)
        remoteVideoView = view.findViewById(R.id.call_remote_video_view)
        remoteVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        durationTextView = view.findViewById(R.id.call_textview_duration)

        statusTextView.text = if (directCall.myRole == DirectCallUserRole.CALLER) {
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
        recordingButton.setOnClickListener(this::onRecordingButtonClicked)
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

    private fun onRecordingButtonClicked(view: View) {
        val button = view as? Button ?: return

        button.isEnabled = false
        if (currentlyRecording) {
            directCall.stopRecording(recordingId = recordingId)
        } else {
            showRecordingTypeSelection()
        }
        currentlyRecording = !currentlyRecording
        button.setText(if (currentlyRecording) R.string.stop_recording else R.string.start_recording)
        button.isEnabled = true
    }

    private fun showRecordingTypeSelection() {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle(R.string.select_recording_type)
        builder.setCancelable(false)
        builder.setItems(recordingTypes.map { it.toString() }.toTypedArray()) { dialog, index ->
            startRecording(recordingTypes[index])
            dialog.dismiss()
        }
        builder.show()
    }

    private fun startRecording(recordingType: RecordingType) {
        val fileName = "${System.nanoTime()}.mp4"
        val file = File(context?.filesDir, fileName)
        val dir = file.parent ?: return
        val recordingOptions = RecordingOptions(
            recordingType = recordingType,
            directoryPath = dir,
            fileName = fileName
        )
        directCall.startRecording(recordingOptions, object : RecordingStartedHandler {
            override fun onRecordingStarted(recordingId: String?, e: SendBirdException?) {
                if (null != e) {
                    showToast("Recording failed to start. Check logcat for details: $e")
                    Log.e("Recording", "onRecordingStarted", e)
                    return
                }
                if (null != recordingId) {
                    showToast("Recording(id=$recordingId) started")
                    this@CallFragment.recordingId = recordingId
                }
            }
        })
    }

    private fun showToast(msg: String) {
        val context = context
        if (null != context) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
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