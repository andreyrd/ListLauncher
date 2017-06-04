package com.amagital.launcher.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

    public static final String KEY_LIST_COLUMNS = "list_columns";
    public static final String KEY_SHOW_ACTION_BAR = "show_action_bar";
    public static final String KEY_SHOW_WALLPAPER = "show_wallpaper";
    public static final String KEY_LAUNCH_ANIM = "launch_anim";
    public static final String KEY_TRANSLUCENT_STATUS = "translucent_status";
    public static final String KEY_TRANSLUCENT_NAVIGATION = "translucent_navigation";

    private SharedPreferences prefs;

    public Settings(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getListColumns() {
        return Integer.valueOf(prefs.getString(KEY_LIST_COLUMNS, "1"));
    }

    public boolean getShowActionBar() {
        return prefs.getBoolean(KEY_SHOW_ACTION_BAR, false);
    }

    public void setShowActionBar(boolean showActionBar) {
        prefs.edit().putBoolean(KEY_SHOW_ACTION_BAR, showActionBar).apply();
    }

    public boolean getShowWallpaper() {
        return prefs.getBoolean(KEY_SHOW_WALLPAPER, true);
    }

    public String getLaunchAnim() {
        return prefs.getString(KEY_LAUNCH_ANIM, "default");
    }

    public boolean getTranslucentStatus() {
        return prefs.getBoolean(KEY_TRANSLUCENT_STATUS, false);
    }

    public boolean getTranslucentNavigation() {
        return prefs.getBoolean(KEY_TRANSLUCENT_NAVIGATION, false);
    }
}
