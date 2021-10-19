package com.app.buna.dontdelay.ads;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

public class MyApplication extends Application {

    private AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(
                this,
                initializationStatus -> {});
        appOpenManager = new AppOpenManager(this);
    }

    public AppOpenManager getAppOpenManager() {
        return appOpenManager;
    }
}