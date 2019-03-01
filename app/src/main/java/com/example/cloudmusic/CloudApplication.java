package com.example.cloudmusic;

import android.app.Application;

import com.coder.zzq.smartshow.core.SmartShow;

public class CloudApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SmartShow.init(this);
    }
}
