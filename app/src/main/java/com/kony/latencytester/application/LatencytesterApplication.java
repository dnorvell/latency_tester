package com.kony.latencytester.application;

import android.app.Application;
import android.content.Context;

/**
 * Application class for the app.
 */
public class LatencyTesterApplication extends Application {

    private static Context sContext;

    public static Context getAppContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

    }

}
