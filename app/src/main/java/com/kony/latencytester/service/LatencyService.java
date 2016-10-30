package com.kony.latencytester.service;

import android.content.Intent;

/**
 * Created by dnorvell on 10/30/16.
 */
public class LatencyService extends BaseService {

    @Override
    public ServiceType getServiceType() {
        return ServiceType.Latency;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
