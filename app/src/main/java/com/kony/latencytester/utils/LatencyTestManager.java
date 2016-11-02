package com.kony.latencytester.utils;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import com.kony.latencytester.entities.Contact;
import com.kony.latencytester.entities.CustomLogicApiResponse;
import com.kony.latencytester.entities.LatencyRecord;
import com.kony.latencytester.entities.SimpleResponse;
import com.kony.latencytester.web.WebApiManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dnorvell on 10/30/16.
 */
public class LatencyTestManager {

    public static final String TAG = "LatencyTestManager";

    private static LatencyTestManager sInstance;
    private static ArrayList<WeakReference<LatencyTestListener>> sListeners = new ArrayList<>();

    private Context mContext;
    private LatencyRecord mRecord;

    public static LatencyTestManager getInstance(Context _context) {
        if (sInstance == null) {
            sInstance = new LatencyTestManager(_context);
        }
        return sInstance;
    }

    public LatencyTestManager(Context _context) {
        mContext = _context;
    }

    public void start() throws NetworkErrorException {

        /*
         * Obtain diagnostical meta data first so we don't skew our timestamps
         */

        // Starting with our connection type
        final String networkType;
        if (Connectivity.isConnected(mContext)) {
            if (Connectivity.isConnectedWifi(mContext)) {
                networkType = "wifi";
            } else if (Connectivity.isConnectedMobile(mContext)) {
                networkType = "mobile";
            } else {
                networkType = "other";
            }
        } else {
            throw new NetworkErrorException();
        }

        // Determine our general latency via ping command
        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
        String pingOutput = Utils.ping(prefs.getString(Constants.PING_HOST, "8.8.8.8"));
        String pingLatency;
        try {
            if(pingOutput != null) {
                pingLatency = pingOutput.split("\n")[1].split("=")[3];
            }
            else {
                pingLatency = "error retrieving ping";
            }
        }
        catch (Exception e) {
            pingLatency = "error retrieving ping";
        }

        // Get our current location
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if(!Build.FINGERPRINT.contains("generic")) { // Common way of determine hardware device vs emulator
            final String ping = pingLatency;
            SingleShotLocationProvider.requestSingleUpdate(locationManager, new SingleShotLocationProvider.LocationCallback() {

                @Override
                public void onNewLocationAvailable(Location location) {
                    // Now that we have our location, lets start our test
                    beginLatencyChecks(location, networkType, ping);
                }
            });
        }

        // We are likely running in an emulator so spoof gps stuff
        else {
            Location location = new Location(""); // Provider is not necessary
            location.setLatitude(1.0);
            location.setLongitude(-1.0);
            beginLatencyChecks(location, networkType, pingLatency);
        }

    }

    public void beginLatencyChecks(Location _location, String _networkType, String _pingLatency) {
        //Create the record object. The start time is set when it is instantiated.
        mRecord = new LatencyRecord(_location, _networkType, _pingLatency);
        simpleApiCheck();
    }

    private void simpleApiCheck() {
        WebApiManager.getInstance().getSimpleApi().enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                SimpleResponse simpleResponse = response.body();
                Log.v(TAG, simpleResponse.toString());

                mRecord.simpleApiLatency = calculateWebcallTimeInMS(response);
                customLogicApiCheck();
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.e(TAG, "Error with simple api: " + t.getLocalizedMessage());
                mRecord.simpleApiLatency = t.getLocalizedMessage();
                customLogicApiCheck();
            }
        });
    }

    private void customLogicApiCheck() {
        WebApiManager.getInstance().postCustomLogicApi().enqueue(new Callback<CustomLogicApiResponse>() {
            @Override
            public void onResponse(Call<CustomLogicApiResponse> call, Response<CustomLogicApiResponse> response) {
                CustomLogicApiResponse customLogicApiResponse = response.body();
                Log.v(TAG, customLogicApiResponse.toString());


                mRecord.customLogicApi = calculateWebcallTimeInMS(response);
                getContact();
            }

            @Override
            public void onFailure(Call<CustomLogicApiResponse> call, Throwable t) {
                Log.e(TAG, "Error with simple api: " + t.getLocalizedMessage());
                mRecord.customLogicApi = t.getLocalizedMessage();
                getContact();
            }
        });
    }

    private void getContact() {
        WebApiManager.getInstance().getContact().enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                Contact contact = response.body();
//                Log.v(TAG, contact.toString());

                mRecord.onlineGetContact = calculateWebcallTimeInMS(response);
                fullOfflineSync();
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.e(TAG, "Error with simple api: " + t.getLocalizedMessage());
                mRecord.onlineGetContact = t.getLocalizedMessage();
                fullOfflineSync();
            }
        });
    }

    private void fullOfflineSync() {
        WebApiManager.getInstance().fullOfflineSync().enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                Contact contact = response.body();
//                Log.v(TAG, contact.toString());

                mRecord.fullOfflineSync = calculateWebcallTimeInMS(response);
                partialOfflineSync();
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.e(TAG, "Error with simple api: " + t.getLocalizedMessage());
                mRecord.fullOfflineSync = t.getLocalizedMessage();
                partialOfflineSync();
            }
        });
    }

    private void partialOfflineSync() {
        WebApiManager.getInstance().partialOfflineSync().enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                Contact contact = response.body();
//                Log.v(TAG, contact.toString());

                mRecord.partialOfflineSync = calculateWebcallTimeInMS(response);
                syncUpload();
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.e(TAG, "Error with simple api: " + t.getLocalizedMessage());
                mRecord.partialOfflineSync = t.getLocalizedMessage();
                syncUpload();
            }
        });
    }

    private void syncUpload() {
        // TODO
        complete();
    }

    private void complete() {
        // Set the ending timestamp
        mRecord.endTime = new Date().getTime();

        // Notify anyone who is listening to this class (Main fragment or background service)
        for (int i = 0; i < sListeners.size(); i++) {
            WeakReference<LatencyTestListener> listenerWeakReference = sListeners.get(i);
            LatencyTestListener listener = listenerWeakReference.get();
            if(listener != null) {
                listener.onLatencyTestComplete(mRecord);
            }
        }
    }

    private String calculateWebcallTimeInMS(Response _response) {
        long startTime = Long.valueOf(_response.headers().get("OkHttp-Sent-Millis"));
        long endTime = Long.valueOf(_response.headers().get("OkHttp-Received-Millis"));
        return String.valueOf(endTime - startTime);
    }

    public interface LatencyTestListener {
        void onLatencyTestComplete(LatencyRecord _latencyRecord);
    }

    public static void addLatencyTestListener(LatencyTestListener _latencyTestListener) {
        // Remove any stale listeners and duplicates. Currently only one instance of a given class is supported.
        for (int i = 0; i < sListeners.size(); i++) {
            WeakReference<LatencyTestListener> listenerWeakReference = sListeners.get(i);
            LatencyTestListener listener = listenerWeakReference.get();
            if (listener == null || listener == _latencyTestListener || listener.getClass() == _latencyTestListener.getClass()) {
                sListeners.remove(i);
                i--;
            }
        }

        // Add the listener we were just given to our static list
        sListeners.add(new WeakReference<>(_latencyTestListener));
    }

    public static void removeLatencyTestListener(LatencyTestListener _latencyTestListener) {
        // Remove any stale listeners and duplicates
        for (int i = 0; i < sListeners.size(); i++) {
            WeakReference<LatencyTestListener> listenerWeakReference = sListeners.get(i);
            LatencyTestListener listener = listenerWeakReference.get();
            if (listener == null || listener == _latencyTestListener || listener.getClass() == _latencyTestListener.getClass()) {
                sListeners.remove(i);
                i--;
            }
        }
    }

}
