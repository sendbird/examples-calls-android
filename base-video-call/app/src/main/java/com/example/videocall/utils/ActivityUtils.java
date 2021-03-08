package com.example.videocall.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.videocall.BaseApplication;
import com.example.videocall.SignInActivity;
import com.example.videocall.main.DialActivity;

public class ActivityUtils {

    public static final int START_SIGN_IN_MANUALLY_ACTIVITY_REQUEST_CODE = 1;

    public static void startSignInActivityForResult(@NonNull Activity activity) {
        Log.i(BaseApplication.TAG, "[ActivityUtils] startSignInManuallyActivityAndFinish()");

        Intent intent = new Intent(activity, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivityForResult(intent, START_SIGN_IN_MANUALLY_ACTIVITY_REQUEST_CODE);
    }

    public static void startSignInActivityAndFinish(@NonNull Activity activity) {
        Log.i(BaseApplication.TAG, "[ActivityUtils] startAuthenticateActivityAndFinish()");

        Intent intent = new Intent(activity, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void startMainActivityAndFinish(@NonNull Activity activity) {
        Log.i(BaseApplication.TAG, "[ActivityUtils] startMainActivityAndFinish()");

        Intent intent = new Intent(activity, DialActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

}