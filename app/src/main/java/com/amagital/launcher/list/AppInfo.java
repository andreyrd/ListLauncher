package com.amagital.launcher.list;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

/**
 * The info necessary to show a list item for the app.
 */
class AppInfo implements Comparable<AppInfo> {

	/** Intent used to start the app. */
	public Intent intent;

	/** Name of the app shown in the list. */
	public String name;

	/**
	 * Compares app names by alphabetical order.
	 * If not an instance of AppInfo, returns 0
	 *
	 * @param another object to compare to
	 * @return result of String.compareTo() on app names
	 */
	@Override
	public int compareTo(AppInfo another) {
		return this.name.toLowerCase().compareTo(another.name.toLowerCase());
	}

    /**
	 * Returns loaded icon or null. If icon is not loaded, it will load in the background and notify
	 * the passed adapter when finished.
	 *
	 * @param context used to get package manager
	 * @param adapter adapter we can notify when finished
     * @return app icon drawable
	 */
    public Drawable getIcon(Context context, BaseAdapter adapter) {
		if (icon == null) {
			loadIcon(context, adapter);
		}

		return icon;
	}

	/** Icon shown next to app name in list. */
	private Drawable icon;

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
}
