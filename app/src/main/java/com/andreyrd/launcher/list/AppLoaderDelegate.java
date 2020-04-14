package com.andreyrd.launcher.list;

import java.util.List;

/**
 * Implemented by classes that want to respond to AppLoader events.
 */
public interface AppLoaderDelegate {

    /** Called when the app loader finishes loading. */
    void appLoaderReloaded(List<AppInfo> apps);
}
