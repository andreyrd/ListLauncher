package com.amagital.listlauncher;

import android.app.Activity;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LauncherAdapter extends BaseAdapter {
    private LauncherApplication application;
    private Activity activity;

    public LauncherAdapter(LauncherApplication application, final Activity activity) {
        this.application = application;
        this.activity = activity;

        LauncherApplication.Callback callback = new LauncherApplication.Callback() {
            @Override
            public void onLoad() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Debug.log("Refreshed apps.");
                        notifyDataSetChanged();
                    }
                });
            }
        };

        application.setCallback(callback);
    }

    @Override
    public int getCount() {
        if (application.isLoaded()) {
            return application.getCache().size();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(activity, R.layout.item, null);
        }

        final App app = application.getCache().get(i);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(app.getIntent());
            }
        });

        ImageView iconView = (ImageView) view.findViewById(R.id.item_icon);
        iconView.setImageDrawable(app.getIcon());

        TextView nameView = (TextView) view.findViewById(R.id.item_name);
        nameView.setText(app.getName());

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
