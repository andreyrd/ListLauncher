package com.amagital.listlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context.getApplicationContext() != null) {
            LauncherApplication application = (LauncherApplication) context.getApplicationContext();
            application.loadApplications();
        }
    }
}
