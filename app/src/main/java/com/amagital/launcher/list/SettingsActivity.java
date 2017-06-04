package com.amagital.launcher.list;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		settings = new Settings(this);

		Preference translucentStatusPreference = findPreference(Settings.KEY_TRANSLUCENT_STATUS);
		Preference translucentNavigationPreference = findPreference(Settings.KEY_TRANSLUCENT_NAVIGATION);

		// Disable on versions of Android that don't support it
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			translucentStatusPreference.setEnabled(false);
			translucentNavigationPreference.setEnabled(false);
		}

		updateActionBarPref();
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
		updateActionBarPref();
	}

	/** Disables the action bar if translucent status/navigation is enabled. */
	private void updateActionBarPref() {
		Preference actionBarPreference = findPreference("show_action_bar");

		if (settings.getTranslucentStatus() || settings.getTranslucentNavigation()) {
			// Need to disable action bar!
			actionBarPreference.setEnabled(false);
			settings.setShowActionBar(false);
		} else {
			actionBarPreference.setEnabled(true);
		}
	}
}
