package com.example.callhistory.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.callhistory.BaseApplication;
import com.example.callhistory.R;
import com.example.callhistory.call.CallService;
import com.example.callhistory.call.SettingsActivity;
import com.example.callhistory.utils.PrefUtils;
import com.example.callhistory.utils.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class DialActivity extends AppCompatActivity {

    private InputMethodManager mInputMethodManager;

    private TextInputEditText mTextInputEditTextUserId;
    private ImageView mImageViewVoiceCall;

    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,   // for VoiceCall and VideoCall
            Manifest.permission.CAMERA          // for VideoCall
    };

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);

        mContext = this;
        Toolbar dialToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(dialToolbar);

        if (mContext != null) {
            mInputMethodManager = (InputMethodManager) (mContext.getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        checkPermissions();
        initializeViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(BaseApplication.TAG, "[MainActivity] onNewIntent()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_call_history:
                intent = new Intent(this, CallHistoryActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void checkPermissions() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(deniedPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                ToastUtils.showToast(mContext, "Permission denied.");
            }
        }
    }

    private void initializeViews(){
        mTextInputEditTextUserId = findViewById(R.id.text_input_edit_text_user_id);
        mImageViewVoiceCall = findViewById(R.id.image_view_voice_call);

        mImageViewVoiceCall.setEnabled(false);

        String savedCalleeId = PrefUtils.getCalleeId(mContext);
        if (!TextUtils.isEmpty(savedCalleeId)) {
            mTextInputEditTextUserId.setText(savedCalleeId);
            mTextInputEditTextUserId.setSelection(savedCalleeId.length());
            mImageViewVoiceCall.setEnabled(true);
        }

        mTextInputEditTextUserId.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mTextInputEditTextUserId.clearFocus();
                if (mInputMethodManager != null) {
                    mInputMethodManager.hideSoftInputFromWindow(mTextInputEditTextUserId.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        mTextInputEditTextUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mImageViewVoiceCall.setEnabled(editable != null && editable.length() > 0);
            }
        });

        mImageViewVoiceCall.setOnClickListener(view1 -> {
            String calleeId = (mTextInputEditTextUserId.getText() != null ? mTextInputEditTextUserId.getText().toString() : "");
            if (!TextUtils.isEmpty(calleeId)) {
                CallService.dial(mContext, calleeId, true);
                PrefUtils.setCalleeId(mContext, calleeId);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allowed = true;

            for (int result : grantResults) {
                allowed = allowed && (result == PackageManager.PERMISSION_GRANTED);
            }

            if (!allowed) {
                ToastUtils.showToast(mContext, "Permission denied.");
            }
        }
    }
}