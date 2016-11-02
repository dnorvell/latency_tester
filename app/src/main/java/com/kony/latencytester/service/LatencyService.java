package com.kony.latencytester.service;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.kony.latencytester.R;
import com.kony.latencytester.entities.LatencyRecord;
import com.kony.latencytester.utils.Constants;
import com.kony.latencytester.utils.LatencyTestManager;
import com.kony.latencytester.utils.LogFile;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dnorvell on 10/30/16.
 */
public class LatencyService extends BaseService implements LatencyTestManager.LatencyTestListener {

    public static final String TAG = "LatencyService";

    ScheduledExecutorService mScheduleTaskExecutor;

    @Override
    public ServiceType getServiceType() {
        return ServiceType.Latency;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Background service created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LatencyTestManager.addLatencyTestListener(this);

        if (mScheduleTaskExecutor == null) {
            mScheduleTaskExecutor = Executors.newScheduledThreadPool(1);
            mScheduleTaskExecutor.scheduleAtFixedRate(performTests(), 0, getScheduleInterval(), TimeUnit.MINUTES);
        }
        else {
            // Interval may have changed so lets restart
            mScheduleTaskExecutor.shutdownNow();
            mScheduleTaskExecutor = Executors.newScheduledThreadPool(1);
            mScheduleTaskExecutor.scheduleAtFixedRate(performTests(), 0, getScheduleInterval(), TimeUnit.MINUTES);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable performTests() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v(TAG, String.valueOf(Thread.currentThread().getId()));
                    LatencyTestManager.getInstance(LatencyService.this).start();
                }
                catch (NetworkErrorException e) {
                    Log.e(TAG, getString(R.string.network_error_help) + e.getLocalizedMessage());
                }
                catch (SecurityException e) {
                    Log.e(TAG, getString(R.string.security_error_help) + e.getLocalizedMessage());
                }
            }
        };
    }

    @Override
    public void onLatencyTestComplete(LatencyRecord _latencyRecord) {
        LogFile.appendLog(_latencyRecord.toString());
    }

    private int getScheduleInterval() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.BACKGROUND_INTERVAL, 30);
    }

    @Override
    public void onDestroy() {
        mScheduleTaskExecutor.shutdownNow();
        Toast.makeText(this, "Background service stopped", Toast.LENGTH_SHORT).show();
        LatencyTestManager.removeLatencyTestListener(this);
        super.onDestroy();
    }

}
