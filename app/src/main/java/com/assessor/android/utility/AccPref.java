package com.assessor.android.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class AccPref {

    private static final String TAG = "zeepref";
    public static final String KEY_PREF_VALUE = "zeepref";

    protected static final String KEY_USER_ID = "userid";
    protected static final String KEY_ACCESS_TOKEN = "acc_token";
    protected static final String KEY_USER_NAME = "username";
    protected static final String KEY_FIRST_NAME = "firstname";
    protected static final String KEY_LAST_NAME = "lastname";
    protected static final String KEY_EMAIL = "email";
    protected static final String KEY_MOBILE = "mobile";
    protected static final String KEY_EXAMID = "eid";
    protected static final String KEY_LAT = "lat";
    protected static final String KEY_LONG = "lon";
    protected static final String KEY_EXAM_SUSPENDED = "exam_susp_";
    protected static final String KEY_EXAM_COMPLETED = "exam_comp_";
    protected static final String KEY_EXAM_VIDEO = "exam_vid_";


    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(KEY_PREF_VALUE, Context.MODE_PRIVATE);
    }


    private static void putLong(Context context, String key, long value) {
        getPreferences(context).edit().putLong(key, value).commit();
    }

    private static long getLong(Context context, String key, long value) {
        return getPreferences(context).getLong(key, value);
    }

    private static void putString(Context context, String key, String value) {
        getPreferences(context).edit().putString(key, value).commit();
    }

    private static String getString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    private static int getInt(Context context, String key) {
        return getPreferences(context).getInt(KEY_PREF_VALUE + key, 0);
    }

    private static void putInt(Context context, String key, int value) {
        getPreferences(context).edit().putInt(KEY_PREF_VALUE + key, value).commit();
    }

    private static boolean getBoolean(Context context, String key, boolean def) {
        return getPreferences(context).getBoolean(KEY_PREF_VALUE + key, def);
    }


    private static void putBoolean(Context context, String key, boolean value) {
        getPreferences(context).edit().putBoolean(KEY_PREF_VALUE + key, value).commit();
    }

    private static String getStringPrefrence(Context context, String type) {
        return getString(context, KEY_PREF_VALUE + type, "");
    }

    private static void setStringPrefrence(Context context, String type, String value) {
        putString(context, KEY_PREF_VALUE + type, value);
    }

    private static void removeValue(Context context, String key) {
        getPreferences(context).edit().remove(KEY_PREF_VALUE + key).commit();
    }

    public static void clear(Context context) {
        getPreferences(context).edit().clear().commit();
    }


    public static void setUserId(Context context, int userId) {
        putInt(context, KEY_USER_ID, userId);
    }


    public static int getUserId(Context context) {
        return getInt(context, KEY_USER_ID);
    }


    public static void setExamId(Context context, int eid) {
        putInt(context, KEY_EXAMID, eid);
    }

    public static int getExamId(Context context) {
        return getInt(context, KEY_EXAMID);
    }


    public static void setUserName(Context context, String userName) {
        if (!TextUtils.isEmpty(userName)) {
            setStringPrefrence(context, KEY_USER_NAME, userName);
        }
    }

    public static String getUserName(Context context) {
        return getStringPrefrence(context, KEY_USER_NAME);
    }

    public static void setAccessToken(Context context, String token) {
        if (!TextUtils.isEmpty(token)) {
            setStringPrefrence(context, KEY_ACCESS_TOKEN, token);
        }
    }

    public static String getAccessToken(Context context) {
        return getStringPrefrence(context, KEY_ACCESS_TOKEN);
    }

    public static void setEmail(Context context, String mail) {
        if (!TextUtils.isEmpty(mail)) {
            setStringPrefrence(context, KEY_EMAIL, mail);
        }
    }

    public static void setSuspended(Context context, int examid, boolean isSuspended) {
        putBoolean(context, KEY_EXAM_SUSPENDED + examid, isSuspended);
    }

    public static boolean isSuspended(Context context, int examid) {
        return getBoolean(context, KEY_EXAM_SUSPENDED + examid, false);
    }

    public static void setExamCompleted(Context context, int examid, boolean isSuspended) {
        putBoolean(context, KEY_EXAM_COMPLETED + examid, isSuspended);
    }

    public static boolean isExamCompleted(Context context, int examid) {
        return getBoolean(context, KEY_EXAM_COMPLETED + examid, false);
    }

    public static String getEmail(Context context) {
        return getStringPrefrence(context, KEY_EMAIL);
    }

    public static void setFirstName(Context context, String value) {
        if (!TextUtils.isEmpty(value)) {
            setStringPrefrence(context, KEY_FIRST_NAME, value);
        }
    }

    public static String getFirstName(Context context) {
        return getStringPrefrence(context, KEY_FIRST_NAME);
    }

    public static void setLastName(Context context, String value) {
        if (!TextUtils.isEmpty(value)) {
            setStringPrefrence(context, KEY_LAST_NAME, value);
        }
    }

    public static String getLastName(Context context) {
        return getStringPrefrence(context, KEY_LAST_NAME);
    }

    public static void setMobile(Context context, String value) {
        if (!TextUtils.isEmpty(value)) {
            setStringPrefrence(context, KEY_MOBILE, value);
        }
    }

    public static String getMobile(Context context) {
        return getStringPrefrence(context, KEY_MOBILE);
    }

    public static void setLat(Context context, String value) {
        if (!TextUtils.isEmpty(value)) {
            setStringPrefrence(context, KEY_LAT, value);
        }
    }

    public static String getLat(Context context) {
        return getStringPrefrence(context, KEY_LAT);
    }

    public static void setLong(Context context, String value) {
        if (!TextUtils.isEmpty(value)) {
            setStringPrefrence(context, KEY_LONG, value);
        }
    }

    public static String getLong(Context context) {
        return getStringPrefrence(context, KEY_LONG);
    }


    public static void logOut(Context context) {
        setStringPrefrence(context, KEY_USER_ID, "");
        setStringPrefrence(context, KEY_USER_NAME, "");
    }
}
