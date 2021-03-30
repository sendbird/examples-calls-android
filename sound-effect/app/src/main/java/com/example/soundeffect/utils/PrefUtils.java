package com.example.soundeffect.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.soundeffect.BaseApplication;

public class PrefUtils {

    private static final String PREF_NAME = "sendbird_calls";

    private static final String PREF_KEY_APP_ID         = "app_id";
    private static final String PREF_KEY_USER_ID        = "user_id";
    private static final String PREF_KEY_ACCESS_TOKEN   = "access_token";
    private static final String PREF_KEY_CALLEE_ID      = "callee_id";
    private static final String PREF_KEY_PUSH_TOKEN     = "push_token";
    private static final String PREF_KEY_RECONNECTING    = "reconnecting";
    private static final String PREF_KEY_RECONNECTED    = "reconnected";
    private static final String PREF_KEY_RINGING   = "ringing";
    private static final String PREF_KEY_DIALING   = "dialing";


    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setAppId(Context context, String appId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_KEY_APP_ID, appId).apply();
    }

    public static String getAppId(Context context) {
        return getSharedPreferences(context).getString(PREF_KEY_APP_ID, BaseApplication.APP_ID);
    }

    public static void setUserId(Context context, String userId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_KEY_USER_ID, userId).apply();
    }

    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString(PREF_KEY_USER_ID, "");
    }

    public static void setAccessToken(Context context, String accessToken) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public static String getAccessToken(Context context) {
        return getSharedPreferences(context).getString(PREF_KEY_ACCESS_TOKEN, "");
    }

    public static void setCalleeId(Context context, String calleeId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_KEY_CALLEE_ID, calleeId).apply();
    }

    public static String getCalleeId(Context context) {
        return getSharedPreferences(context).getString(PREF_KEY_CALLEE_ID, "");
    }

    public static void setPushToken(Context context, String pushToken) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_KEY_PUSH_TOKEN, pushToken).apply();
    }

    public static String getPushToken(Context context) {
        return getSharedPreferences(context).getString(PREF_KEY_PUSH_TOKEN, "");
    }

    public static void setReconnecting(Context context, Boolean reconnecting){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_KEY_RECONNECTING, reconnecting).apply();
    }

    public static boolean getReconnecting(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_KEY_RECONNECTING, false);
    }

    public static void setReconnected(Context context, Boolean reconnected){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_KEY_RECONNECTED, reconnected).apply();
    }

    public static boolean getReconnected(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_KEY_RECONNECTED, false);
    }

    public static boolean getRinging(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_KEY_RINGING, false);
    }

    public static void setRinging(Context context, Boolean ringing){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_KEY_RINGING, ringing).apply();
    }

    public static boolean getDialing(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_KEY_DIALING, false);
    }

    public static void setDialing(Context context, Boolean dialing){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_KEY_RINGING, dialing).apply();
    }

}
