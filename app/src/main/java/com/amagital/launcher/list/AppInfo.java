package com.amagital.launcher.list;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

/**
 * The info necessary to show a list item for the app.
 *
 * @author  Andrey Radchishin
 * @version 1.0
 */
class AppInfo implements Comparable {
	private Intent intent;
	private String name;
	private Drawable icon;

	/**
	 * @return intent   for starting the app.
	 */
	public Intent getIntent() {
		return intent;
	}

	/**
	 * @param intent    intent for starting the app
	 */
	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	/**
	 * @return  display name of the app
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name  display name of the app
	 */
	public void setName(String name) {
		this.name = name;
	}

    /**
	 * Returns loaded icon or null. If icon is not loaded, it will load in the background and notify
	 * the passed adapter when finished.
	 *
	 * @param context used to get package manager
	 * @param adapter adapter we can notify when finished
     * @return        app icon drawable
     */
	public Drawable getIcon(Context context, BaseAdapter adapter) {
		if (icon == null) {
			loadIcon(context, adapter);
		}

		return icon;
	}

	/**
	 * Loads app icon in the background.
	 *
	 * @param context used to get package manager
	 * @param adapter adapter we can notify when finished
	 */
	private void loadIcon(final Context context, final BaseAdapter adapter) {
		AsyncTask<Void, Void, Drawable> task = new AsyncTask<Void, Void, Drawable>() {
			@Override
			protected Drawable doInBackground(Void... params) {
				try {
					return context.getPackageManager().getActivityIcon(intent);
				} catch (PackageManager.NameNotFoundException e) {
					return context.getPackageManager().getDefaultActivityIcon();
				}
			}

			@Override
			protected void onPostExecute(Drawable drawable) {
				icon = drawable;
				adapter.notifyDataSetChanged();
			}
		};

		task.execute();
	}


	/**
	 * Compares app names by alphabetical order.
	 * If not an instance of AppInfo, returns 0
	 *
	 * @param another	object to compare to
	 * @return			result of String.compareTo() on app names
	 */
	@Override
	public int compareTo(Object another) {
		if (another instanceof AppInfo) {
			return this.name.toLowerCase().compareTo(((AppInfo) another).name.toLowerCase());
		} else {
			return 0;
		}
	}
}
