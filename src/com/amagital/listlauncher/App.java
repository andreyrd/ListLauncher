package com.amagital.listlauncher;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class App {
    private Intent intent;
    private Drawable icon;
    private String name;

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
