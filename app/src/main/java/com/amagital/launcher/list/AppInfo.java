package com.amagital.launcher.list;

import android.content.Intent;
import android.graphics.drawable.Drawable;

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
     * @return  app icon drawable
     */
	public Drawable getIcon() {
		return icon;
	}

    /**
     * @param icon  app icon drawable
     */
	public void setIcon(Drawable icon) {
		this.icon = icon;
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
