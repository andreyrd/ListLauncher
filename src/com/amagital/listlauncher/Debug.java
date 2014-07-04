package com.amagital.listlauncher;

import android.util.Log;

public class Debug {
    public static final String TAG = "ListLauncher";
    public static final boolean LOGS = true;

    public static void log(String msg) {
        if (LOGS) Log.d(TAG, msg);
    }
}
