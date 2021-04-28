package com.example.fullscreenincomingcall.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.accessibility.AccessibilityEventCompat;

import com.example.fullscreenincomingcall.R;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;

import org.w3c.dom.Text;

public class IncomingCallActivity extends AppCompatActivity {

    //+ Views
    ImageView mImageViewProfile;
    TextView mTextViewUserId;

    ImageView mImageViewAccept;
    ImageView mImageViewDecline;
    //- Views

    DirectCall mDirectCall;

    String remoteUserId;
    CallActivity.STATE mState;
    private String mCallId;
    boolean mIsVideoCall;
    String mCalleeIdToDial;
    private boolean mDoDial;
    private boolean mDoAccept;
    protected boolean mDoLocalVideoStart;

    private boolean mDoEnd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(AccessibilityEventCompat.TYPE_WINDOWS_CHANGED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        registerReceiver(endIncomingCallActivity, new IntentFilter("endIncomingCallActivity"));

        init();
        initViews();
        setViews();
    }

    private final BroadcastReceiver endIncomingCallActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(endIncomingCallActivity);
    }

    protected void init() {
        Intent intent = getIntent();

        remoteUserId = intent.getStringExtra(CallService.EXTRA_REMOTE_NICKNAME_OR_USER_ID);
        mState = (CallActivity.STATE) intent.getSerializableExtra(CallService.EXTRA_CALL_STATE);
        mCallId = intent.getStringExtra(CallService.EXTRA_CALL_ID);
        mIsVideoCall = intent.getBooleanExtra(CallService.EXTRA_IS_VIDEO_CALL, false);
        mCalleeIdToDial = intent.getStringExtra(CallService.EXTRA_CALLEE_ID_TO_DIAL);
        mDoDial = intent.getBooleanExtra(CallService.EXTRA_DO_DIAL, false);
        mDoAccept = intent.getBooleanExtra(CallService.EXTRA_DO_ACCEPT, false);
        mDoLocalVideoStart = intent.getBooleanExtra(CallService.EXTRA_DO_LOCAL_VIDEO_START, false);

        mDoEnd = intent.getBooleanExtra(CallService.EXTRA_DO_END, false);

        if (mCallId != null) {
            mDirectCall = SendBirdCall.getCall(mCallId);
        }
    }

    protected void initViews() {
        mImageViewProfile = findViewById(R.id.image_view_profile);
        mTextViewUserId = findViewById(R.id.text_view_user_id);
        mImageViewAccept = findViewById(R.id.image_view_accept);
        mImageViewDecline = findViewById(R.id.image_view_decline);
    }

    protected void setViews() {
        mTextViewUserId.setText(remoteUserId);
        mImageViewAccept.setOnClickListener(view -> {
            Intent intent = new Intent(this, VideoCallActivity.class);
            intent.putExtra(CallService.EXTRA_CALL_STATE, mState);
            intent.putExtra(CallService.EXTRA_CALL_ID, mCallId);
            intent.putExtra(CallService.EXTRA_IS_VIDEO_CALL, mIsVideoCall);
            intent.putExtra(CallService.EXTRA_CALLEE_ID_TO_DIAL, mCalleeIdToDial);
            intent.putExtra(CallService.EXTRA_DO_DIAL, mDoDial);
            intent.putExtra(CallService.EXTRA_DO_ACCEPT, mDoAccept);
            intent.putExtra(CallService.EXTRA_DO_LOCAL_VIDEO_START, mDoLocalVideoStart);
            intent.putExtra(CallService.EXTRA_DO_END, mDoEnd);
            startActivity(intent);
            finish();

        });
        mImageViewDecline.setOnClickListener(view -> {
            mDirectCall.end();
            finish();
        });
    }
}
