package com.example.callhistory.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.callhistory.BaseApplication;
import com.sendbird.calls.DirectCallLog;

public class BroadcastUtils {

    public static final String INTENT_ACTION_ADD_CALL_LOG = "com.example.callhistory.intent.action.ADD_CALL_LOG";
    public static final String INTENT_EXTRA_CALL_LOG = "call_log";

    public static void sendCallLogBroadcast(Context context, DirectCallLog callLog) {
        if (context != null && callLog != null) {
            Log.i(BaseApplication.TAG, "[BroadcastUtils] sendCallLogBroadcast()");

            Intent intent = new Intent(INTENT_ACTION_ADD_CALL_LOG);
            intent.putExtra(INTENT_EXTRA_CALL_LOG, callLog);
            context.sendBroadcast(intent);
        }
    }
}
