package com.andreyrd.launcher.list;

import android.content.Context;
import android.util.Pair;

public class Utils {

    public static Pair<Integer, Integer> launcherAnimationResourceFromString(String anim) {
        Integer animIn = null;
        Integer animOut = null;

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

        return Pair.create(animIn, animOut);
    }

    public static Pair<Integer, Integer> appAnimationResourceFromString(String anim) {
        Integer animIn = null;
        Integer animOut = null;

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

        if (animIn == null) {
            animIn = R.anim.activity_open_enter;
            animOut = R.anim.activity_open_exit;
        }

        return Pair.create(animIn, animOut);
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getNavigationBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
