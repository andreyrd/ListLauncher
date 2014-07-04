package com.amagital.listlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class LauncherActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.launcher);

        ListView listView = (ListView) findViewById(R.id.launcher_list);
        listView.setAdapter(new LauncherAdapter((LauncherApplication) getApplication(), this));
    }
}
