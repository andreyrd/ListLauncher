package com.amagital.launcher.list;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages loading the list of apps.
 */
public class AppLoader {

    public AppLoaderDelegate delegate;

    /** Reload apps and notify delegate when done. */
    public void reload() {
        new AsyncTask<Void, Void, List<AppInfo>>() {
            @Override
            protected List<AppInfo> doInBackground(Void... voids) {
                return loadInBackground();
            }

            @Override
            protected void onPostExecute(List<AppInfo> apps) {
                if (delegate != null) {
                    delegate.appLoaderReloaded(apps);
                } else {
                    Log.e("ListLauncher", "AppLoader delegate not set!");
                }
            }
        }.execute();
    }

    public AppLoader(Context context) {
        this.context = context;
        settings = new Settings(context);
    }

    private Context context;
    private Settings settings;

    private List<AppInfo> loadInBackground() {
        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        List<AppInfo> apps = new ArrayList<>(packages.size());

        for (ApplicationInfo info : packages) {
            Intent intent = pm.getLaunchIntentForPackage(info.packageName);
            String name = info.loadLabel(pm).toString();

            // Make sure intent is not null and skip our own package
            if (intent != null && !info.packageName.equals(BuildConfig.APPLICATION_ID)) {

                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                AppInfo appInfo = new AppInfo();

                appInfo.name = name;
                appInfo.intent = intent;

                apps.add(appInfo);
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        /// Add settings activity if the action bar is off
        if (!settings.getShowActionBar()) {
            AppInfo appInfo = new AppInfo();
            appInfo.name = context.getString(R.string.app_settings);
            appInfo.intent = new Intent(context, SettingsActivity.class);
            apps.add(appInfo);
        }

        Collections.sort(apps);
        return apps;
    }
}