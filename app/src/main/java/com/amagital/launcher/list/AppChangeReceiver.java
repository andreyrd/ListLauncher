package com.amagital.launcher.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Receiver that responds when an app is installed/removed.
 */
public class AppChangeReceiver extends BroadcastReceiver {

    /** Delegate for responding to events. */
    public AppChangeReceiverDelegate delegate;

    /** Register self with a context. */
    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        context.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (delegate != null) {
            delegate.appChangeReceiverChanged();
        } else {
            Log.e("ListLauncher", "AppChangeReceiver has no delegate!");
        }
    }
}
