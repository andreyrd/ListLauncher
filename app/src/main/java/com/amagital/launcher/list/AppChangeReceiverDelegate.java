package com.amagital.launcher.list;

/** Delegate for responding to AppChangeReceiver events */
public interface AppChangeReceiverDelegate {

    /** Called when an app has been added/removed. */
    void appChangeReceiverChanged();
}
