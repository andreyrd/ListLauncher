package com.amagital.launcher.list;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LauncherActivity extends Activity {
	private ArrayList<AppInfo> appInfoList;

	private LauncherAdapter listAdapter;

    private boolean prefShowActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.launcher);

		// Initialize the array with 0 capacity (will ensureCapacity later)
		appInfoList = new ArrayList<>(0);

		ListView listView = (ListView) findViewById(R.id.launcher_list);
		listAdapter = new LauncherAdapter(this, appInfoList);

		listView.setAdapter(listAdapter);

		// Launch the app intent when the item is clicked
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(appInfoList.get(position).getIntent());
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {
				showDetailsDialog(appInfoList.get(i));
				return true;
			}
		});
    }

	private ChangeReceiver changeReceiver;

    @Override
	protected void onResume() {
		super.onResume();

        // Show wallpaper according to settings
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("show_wallpaper", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                LayerDrawable wallpaper = new LayerDrawable(new Drawable[] {
                        getWallpaper(),
                        new ColorDrawable(getResources().getColor(R.color.wallpaper_darken))
                });

                findViewById(R.id.launcher_list).setBackground(wallpaper);
            } else {
                findViewById(R.id.launcher_list).setBackgroundDrawable(getWallpaper());
            }
        } else {
            findViewById(R.id.launcher_list).setBackgroundColor(Color.TRANSPARENT);
        }

        prefShowActionBar = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("show_action_bar", false);

        // Hide action bar depending on settings
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (prefShowActionBar) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }

		// Receiver for package changes
		changeReceiver = new ChangeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(changeReceiver, filter);

		loadAppInfo();

        int animIn = -1;
        int animOut = -1;

        String anim = PreferenceManager.getDefaultSharedPreferences(this).getString("launch_anim", "default");

        switch (anim) {
            case "none":
                animIn = 0;
                animOut = 0;
                break;
            case "fade":
                animIn = R.anim.fade_in;
                animOut = R.anim.fade_out;
                break;
            case "zoom_in":
                animIn = 0;
                animOut = R.anim.zoom_out;
                break;
            case "zoom_out":
                animIn = R.anim.zoom_in;
                animOut = R.anim.fade_out;
                break;
        }

        if (animIn >= 0 && animOut >= 0) {
            overridePendingTransition(animIn, animOut);
        }
    }

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(changeReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_main_settings:
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void loadAppInfo() {
		AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				PackageManager pm = getPackageManager();

				List<ApplicationInfo> packages = pm.getInstalledApplications(0);

				ArrayList<AppInfo> updateList = new ArrayList<>(packages.size());

				for (ApplicationInfo info : packages) {
					Intent intent = pm.getLaunchIntentForPackage(info.packageName);
					String name = info.loadLabel(pm).toString();

					if (intent != null && name != null) {
						intent.setAction(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_LAUNCHER);

						AppInfo appInfo = new AppInfo();

						appInfo.setName(name);
						appInfo.setIntent(intent);
						appInfo.setIcon(info.loadIcon(pm));

						updateList.add(appInfo);
					}
				}

                if (!prefShowActionBar) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.setName(getString(R.string.app_settings));
                    appInfo.setIntent(new Intent(LauncherActivity.this, SettingsActivity.class));
                    appInfo.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
                    updateList.add(appInfo);
                }

				Collections.sort(updateList);
				appInfoList = updateList;

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				listAdapter.notifyDataSetChanged(appInfoList);
			}
		};

		task.execute();
	}

	@Override
	public void startActivity(Intent intent) {
		int animIn = -1;
		int animOut = -1;

	    String anim = PreferenceManager.getDefaultSharedPreferences(this).getString("launch_anim", "default");

        switch (anim) {
            case "none":
                animIn = 0;
                animOut = 0;
                break;
            case "fade":
                animIn = R.anim.fade_in;
                animOut = R.anim.fade_out;
                break;
            case "zoom_in":
                animIn = R.anim.zoom_in;
                animOut = R.anim.activity_open_exit;
                break;
            case "zoom_out":
                animIn = 0;
                animOut = R.anim.zoom_out;
                break;
        }

        if (animIn < 0 && animOut < 0) {
            animIn = R.anim.activity_open_enter;
            animOut = R.anim.activity_open_exit;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Cleaner way of doing animations for 4.1 and higher
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, animIn, animOut);
            startActivity(intent, options.toBundle());
        } else {
            super.startActivity(intent);
            overridePendingTransition(animIn, animOut);
        }
    }

	private class ChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadAppInfo();
		}
	}

	private void showDetailsDialog(final AppInfo info) {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setItems(R.array.details, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Uri packageUri = Uri.fromParts("package", info.getIntent().getPackage(), null);

						switch (which) {
							case 0:
								// Open
								startActivity(info.getIntent());
								break;
							case 1:
								// Uninstall
								Intent deleteIntent = new Intent(Intent.ACTION_DELETE, packageUri);
								startActivity(deleteIntent);
								break;
							case 2:
								// App Info
								Intent infoIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
										packageUri);
								startActivity(infoIntent);
								break;
						}
					}
				}).create();

		dialog.show();
	}

	@Override
	public void onBackPressed() {
		// Do nothing. Hehe.
	}
}
