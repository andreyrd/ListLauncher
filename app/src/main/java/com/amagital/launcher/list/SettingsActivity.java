package com.amagital.launcher.list;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Andrey on 9/2/2014.
 */
public class SettingsActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
