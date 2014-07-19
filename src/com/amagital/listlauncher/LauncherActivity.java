package com.amagital.listlauncher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LauncherActivity extends Activity {
	private Context context;

	private static class AppInfo implements Comparable {
		private Intent intent;
		private String name;
		private Drawable icon;

		public Intent getIntent() {
			return intent;
		}

		public void setIntent(Intent intent) {
			this.intent = intent;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Drawable getIcon() {
			return icon;
		}

		public void setIcon(Drawable icon) {
			this.icon = icon;
		}

		@Override
		public int compareTo(Object another) {
			if (another instanceof AppInfo) {
				return this.name.compareTo(((AppInfo) another).name);
			} else {
				return 0;
			}
		}
	}

	private ArrayList<AppInfo> appInfoList;

	private LauncherAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// Set up a pointer to the context for anonymous classes
		this.context = this;

        setContentView(R.layout.launcher);

		// Initialize the array with 0 capacity (will ensureCapacity later)
		appInfoList = new ArrayList<AppInfo>(0);

		ListView listView = (ListView) findViewById(R.id.launcher_list);
		listAdapter = new LauncherAdapter();

		listView.setAdapter(listAdapter);

		// Launch the app intent when the item is clicked
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(appInfoList.get(position).getIntent());
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
		Thread thread = new Thread() {
			@Override
			public void run() {
				PackageManager pm = getPackageManager();

				List<ApplicationInfo> packages = pm.getInstalledApplications(0);

				final ArrayList<AppInfo> updateList = new ArrayList<AppInfo>(packages.size());

				for (ApplicationInfo info : packages) {
					Intent intent = pm.getLaunchIntentForPackage(info.packageName);
					String name = info.loadLabel(pm).toString();

					if (intent != null && name != null) {
						AppInfo appInfo = new AppInfo();

						appInfo.setName(name);
						appInfo.setIntent(intent);
						appInfo.setIcon(info.loadIcon(pm));

						updateList.add(appInfo);
					}
				}

				Collections.sort(updateList);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						appInfoList = updateList;
						listAdapter.notifyDataSetChanged();
					}
				});
			}
		};

		thread.start();
	}

	private class LauncherAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return appInfoList.size();
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = View.inflate(context, R.layout.item, null);
			}

			TextView nameView = (TextView) view.findViewById(R.id.item_name);
			ImageView iconView = (ImageView) view.findViewById(R.id.item_icon);

			AppInfo info = appInfoList.get(position);

			nameView.setText(info.getName());
			iconView.setImageDrawable(info.getIcon());

			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	private class ChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadAppInfo();
		}
	}
}
