package com.amagital.listlauncher;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LauncherApplication extends Application {
    public static interface Callback {
        public void onLoad();
    }

    private boolean loaded;

    private List<App> cache;
    private Callback callback;

    @Override
    public void onCreate() {
        super.onCreate();

        loadApplications();
    }

    public void loadApplications() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = getPackageManager();
                List<ApplicationInfo> infoList = pm.getInstalledApplications(0);

                cache = new ArrayList<App>();

                for (ApplicationInfo info : infoList) {
                    String packageName = info.packageName;

                    Intent intent = pm.getLaunchIntentForPackage(packageName);
                    if (intent != null) {
                        App app = new App();
                        app.setName(info.loadLabel(pm).toString());
                        app.setIcon(info.loadIcon(pm));
                        app.setIntent(intent);
                        cache.add(app);
                    }


                }

                Comparator<App> comparator = new Comparator<App>() {
                    @Override
                    public int compare(App lhs, App rhs) {
                        return (lhs.getName().compareTo(rhs.getName()));
                    }
                };

                Collections.sort(cache, comparator);

                if (callback != null) callback.onLoad();
                loaded = true;
            }
        });

        thread.start();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public List<App> getCache() {
        return cache;
    }
}
