package com.kony.latencytester.service;

import android.accounts.NetworkErrorException;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.kony.latencytester.R;
import com.kony.latencytester.activity.MainActivity;
import com.kony.latencytester.application.BaseApplication;
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
        setupNotifications();
        Toast.makeText(this, "Background service created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LatencyTestManager.addLatencyTestListener(this);
        showNotification();

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

    public static boolean isRunning() {
        LatencyService latencyService = getReference();
        return latencyService != null;
    }

    @Override
    public void onLatencyTestComplete(LatencyRecord _latencyRecord) {
//        LogFile.appendLog(_latencyRecord.toString());
        LogFile.appendLog(_latencyRecord.toCsvLine());
    }

    private int getScheduleInterval() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.BACKGROUND_INTERVAL, 30);
    }

    private static final int NOTIFICATION = 1;
    public static final String CLOSE_ACTION = "close";

    @Nullable
    private NotificationManager mNotificationManager = null;
    private final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);

    private void setupNotifications() { //called in onCreate()
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
        PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .setAction(CLOSE_ACTION),
                0);
        mNotificationBuilder
                .setSmallIcon(R.drawable.ic_speedometer)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        getString(R.string.stop_service), pendingCloseIntent)
                .setOngoing(true);
    }

    private void showNotification() {
        mNotificationBuilder.setContentText(getText(R.string.background_service_running));
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION, mNotificationBuilder.build());
        }
    }

    @Override
    public void onDestroy() {
        mScheduleTaskExecutor.shutdownNow();

        // Remove the service notification
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION);
        }

        Toast.makeText(this, "Background service stopped", Toast.LENGTH_SHORT).show();
        LatencyTestManager.removeLatencyTestListener(this);
        super.onDestroy();
    }

    private static LatencyService getReference() {
        return (LatencyService) BaseApplication.getService(ServiceType.Latency);
    }

}
