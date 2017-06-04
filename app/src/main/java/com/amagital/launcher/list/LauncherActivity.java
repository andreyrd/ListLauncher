package com.amagital.launcher.list;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Main screen. Shows a list of apps!
 */
public class LauncherActivity extends Activity implements AppLoaderDelegate,
		AppChangeReceiverDelegate, AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener {

    private ImageView backgroundView;
	private GridView gridView;
	private ProgressBar progressView;

	private AppLoader appLoader;
	private AppChangeReceiver appChangeReceiver;
	private LauncherAdapter gridAdapter;
	private Settings settings;

	private List<AppInfo> apps = new ArrayList<>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.launcher);

        backgroundView = (ImageView) findViewById(R.id.launcher_bg);
		gridView = (GridView) findViewById(R.id.launcher_list);
		progressView = (ProgressBar) findViewById(R.id.launcher_progress);

		appLoader = new AppLoader(this);
		appLoader.delegate = this;

		appChangeReceiver = new AppChangeReceiver();
		appChangeReceiver.delegate = this;

		gridAdapter = new LauncherAdapter(this, apps);
		gridView.setAdapter(gridAdapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(this);

		settings = new Settings(this);
    }

	@Override
	public void appLoaderReloaded(List<AppInfo> apps) {
		this.apps = apps;
		progressView.setVisibility(View.GONE);
		gridAdapter.notifyDataSetChanged(apps);
	}

	@Override
	public void appChangeReceiverChanged() {
		appLoader.reload();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		startActivity(apps.get(position).intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		showDetailsDialog(apps.get(position));
		return true;
	}

    @Override
	protected void onResume() {
		super.onResume();

		appLoader.reload();

		appChangeReceiver.register(this);
		applySettings();
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(appChangeReceiver);
	}

	private void applySettings() {
		// Show Action Bar
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			if (settings.getShowActionBar()) {
				actionBar.show();
			} else {
				actionBar.hide();
			}
		}

		// Show Wallpaper
		if (settings.getShowWallpaper()) {
			// Darken the wallpaper
		    LayerDrawable wallpaper = new LayerDrawable(new Drawable[] {
					getWallpaper(),
					new ColorDrawable(getResources().getColor(R.color.wallpaper_darken))
		    });

			backgroundView.setImageDrawable(wallpaper);
		} else {
			backgroundView.setImageDrawable(null);
		}

		// Launch Animation
		Pair<Integer, Integer> resources
				= Utils.launcherAnimationResourceFromString(settings.getLaunchAnim());

		Integer animIn = resources.first;
		Integer animOut = resources.second;

		if (animIn != null && animOut != null) {
			overridePendingTransition(animIn, animOut);
		}

		// Columns
		gridView.setNumColumns(settings.getListColumns());

		// Translucent status / navigation
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();

			boolean translucentStatus = settings.getTranslucentStatus();

			if (translucentStatus) {
				window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			} else {
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}

			boolean translucentNavigation = settings.getTranslucentNavigation();

			if (translucentNavigation) {
				window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				gridView.setPadding(0, gridView.getPaddingTop(), 0, Utils.getNavigationBarHeight(this));
			} else {
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				gridView.setPadding(0, gridView.getPaddingTop(), 0, 0);
			}

			// Padding on top is needed if either one is set, since that makes the grid go outside
			// of decor
			if (translucentStatus || translucentNavigation) {
				gridView.setPadding(0, Utils.getStatusBarHeight(this), 0, gridView.getPaddingBottom());
				gridView.setClipToPadding(false);
			} else {
				gridView.setPadding(0, 0, 0, 0);
				gridView.setClipToPadding(true);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_main_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void startActivity(Intent intent) {
		Pair<Integer, Integer> resources = Utils.appAnimationResourceFromString(settings.getLaunchAnim());

		int animIn = resources.first;
		int animOut = resources.second;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Cleaner way of doing animations for 4.1 and higher
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, animIn, animOut);
            startActivity(intent, options.toBundle());
        } else {
            super.startActivity(intent);
            overridePendingTransition(animIn, animOut);
        }
    }

	private void showDetailsDialog(final AppInfo info) {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setItems(R.array.details, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Uri packageUri = Uri.fromParts("package", info.intent.getPackage(), null);

						switch (which) {
							case 0:
								// Open
								startActivity(info.intent);
								break;
							case 1:
								// Uninstall
								Intent deleteIntent = new Intent(Intent.ACTION_DELETE, packageUri);
								startActivity(deleteIntent);
								break;
							case 2:
								// App Info
								Intent infoIntent = new Intent(
										android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
										packageUri
								);
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
