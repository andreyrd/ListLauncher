package com.amagital.launcher.list;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by Andrey on 9/2/2014.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		Preference translucentStatusPreference = findPreference("translucent_status");
		Preference translucentNavigationPreference = findPreference("translucent_navigation");

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			translucentStatusPreference.setEnabled(false);
			translucentNavigationPreference.setEnabled(false);
		}

		updateActionBar();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		updateActionBar();
	}

	private void updateActionBar() {
		boolean translucentStatus = getPreferenceScreen().getSharedPreferences().getBoolean("translucent_status", false);
		boolean translucentNavigation = getPreferenceScreen().getSharedPreferences().getBoolean("translucent_navigation", false);

		Preference actionBarPreference = findPreference("show_action_bar");

		if (translucentStatus || translucentNavigation) {
			// Need to disable action bar!
			actionBarPreference.setEnabled(false);
			getPreferenceScreen().getSharedPreferences().edit().putBoolean("show_action_bar", false).apply();
		} else {
			actionBarPreference.setEnabled(true);
		}
	}
}
