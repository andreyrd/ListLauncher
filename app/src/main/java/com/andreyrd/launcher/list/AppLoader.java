package com.andreyrd.launcher.list;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages loading the list of apps.
 */
public class AppLoader {

    public AppLoaderDelegate delegate;

    public AppLoader(Context context) {
        this.context = context;
        settings = new Settings(context);
    }
    /** Reload apps and notify delegate when done. */
    public void reload() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                AppDatabase.Helper dbh = new AppDatabase.Helper(context);
                loaded(loadFromDatabase(dbh));

                List<AppInfo> apps = loadFromPackageManager();
                loaded(apps);
                saveToDatabase(dbh, apps);
            }
        };

        thread.start();
    }

    public void loaded(final List<AppInfo> apps) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable(){
            public void run(){
                if (delegate != null) {
                    delegate.appLoaderReloaded(apps);
                }
            }
        });
    }

    private Context context;
    private Settings settings;

    private List<AppInfo> loadFromDatabase(AppDatabase.Helper dbh) {
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor cursor = db.query(
                AppDatabase.Columns.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        List<AppInfo> apps = new ArrayList<>();

        while(cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(AppDatabase.Columns.COLUMN_NAME_NAME)
            );

            String intentUri = cursor.getString(
                    cursor.getColumnIndexOrThrow(AppDatabase.Columns.COLUMN_NAME_INTENT)
            );

            try {
                Intent intent = Intent.parseUri(intentUri, 0);

                AppInfo app = new AppInfo();
                app.name = name;
                app.intent = intent;
                apps.add(app);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        db.close();

        return apps;
    }

    private List<AppInfo> loadFromPackageManager() {
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
                appInfo.packageName = info.packageName;
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
            appInfo.packageName = "com.amagital.launcher.list.settings";
            apps.add(appInfo);
        }

        Collections.sort(apps);
        return apps;
    }

    private void saveToDatabase(AppDatabase.Helper dbh, List<AppInfo> apps) {
        SQLiteDatabase db = dbh.getWritableDatabase();

        db.delete(AppDatabase.Columns.TABLE_NAME, null, null);

        for (AppInfo app : apps) {
            ContentValues values = new ContentValues();

            values.put(AppDatabase.Columns.COLUMN_NAME_NAME, app.name);
            values.put(AppDatabase.Columns.COLUMN_NAME_PACKAGE, app.packageName);
            values.put(AppDatabase.Columns.COLUMN_NAME_INTENT, app.intent.toUri(0));

            db.insert(AppDatabase.Columns.TABLE_NAME, null, values);
        }

        db.close();
    }
}