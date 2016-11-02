package com.kony.latencytester.utils;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;

public class SingleShotLocationProvider {

    public interface LocationCallback {
        void onNewLocationAvailable(Location location);
    }

    public static void requestSingleUpdate(LocationManager _locationManager, final LocationCallback _locationCallback) throws SecurityException {

        boolean isNetworkEnabled = _locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            _locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    _locationCallback.onNewLocationAvailable(location);
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
                @Override
                public void onProviderEnabled(String provider) {

                }
                @Override
                public void onProviderDisabled(String provider) {

                }

            }, Looper.getMainLooper());
        }

        else {

            boolean isGPSEnabled = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                _locationManager.requestSingleUpdate(criteria,  new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        _locationCallback.onNewLocationAvailable(location);
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                    @Override
                    public void onProviderEnabled(String provider) {}
                    @Override
                    public void onProviderDisabled(String provider) {}

                }, Looper.getMainLooper());
            }
        }

    }

}