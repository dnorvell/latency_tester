package com.kony.latencytester.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.kony.latencytester.application.BaseApplication;

/**
 * All services inside the app should subclass this one. It will provide useful logging and common methods.
 */
public abstract class BaseService extends Service {

    /**
     * A string that represents this class name that should be used as the tag for all logging.
     */
    protected final String mTag = getTag();

    /**
     * Helper method to get a useful tag for this class. This tag should then be used for all logs.
     * By default this tag is just the class name.
     *
     * @return A tag that can be used for all logging.
     */
    private String getTag() {
        return getClass().getSimpleName();
    }

    /**
     * Upon its creation, registers this service with the application to ensure that a reference is present
     */
    protected void registerService() {
        BaseApplication.registerService(this);
    }

    /**
     * An optional override method that any services who extend this one can implement should immediate
     * action need to be taken after the service has been started and registered with the application.
     */
    protected void onServiceAvailable() {

    }

    /**
     * Upon its destruction, unregisters this service with the application
     */
    protected void unRegisterService() {
        BaseApplication.unRegisterService(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(mTag, getClass().getSimpleName() + " service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerService();
        onServiceAvailable();
        Log.v(mTag, getClass().getSimpleName() + " service started");
        return START_NOT_STICKY;
    }

    public abstract ServiceType getServiceType();

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(mTag, getClass().getSimpleName() + "** HELP I HAVE NO MEMORY I MIGHT DIE **");
    }

    @Override
    public void onDestroy() {
        unRegisterService();
        super.onDestroy();
        Log.v(mTag, getClass().getSimpleName() + " service destroyed");
    }

}
