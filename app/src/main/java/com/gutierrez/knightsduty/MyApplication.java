package com.gutierrez.knightsduty;

import android.app.Application;
import android.content.Context;

/*
    Helper class to fetch the application context easily
 */
public class MyApplication extends Application
{
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
