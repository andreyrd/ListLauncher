package com.amagital.listlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.*;

import java.util.*;

public class LauncherActivity extends Activity {
	private ArrayList<AppInfo> appInfoList;

	private LauncherAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.launcher);

		// Initialize the array with 0 capacity (will ensureCapacity later)
		appInfoList = new ArrayList<AppInfo>(0);

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

		// Receiver for package changes
		changeReceiver = new ChangeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(changeReceiver, filter);

		loadAppInfo();
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(changeReceiver);
	}

	private void loadAppInfo() {
		AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				PackageManager pm = getPackageManager();

				List<ApplicationInfo> packages = pm.getInstalledApplications(0);

				ArrayList<AppInfo> updateList = new ArrayList<AppInfo>(packages.size());

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
