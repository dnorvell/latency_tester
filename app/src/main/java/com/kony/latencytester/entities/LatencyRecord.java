package com.kony.latencytester.entities;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by dnorvell on 10/30/16.
 */
public class LatencyRecord {

    public long startTime;
    public long endTime;
    public String networkType;
    public double latitude;
    public double longitude;
    public String pingLatency;
    public String simpleApiLatency;
    public String customLogicApi;
    public String onlineGetContact;
    public String fullOfflineSync;
    public String partialOfflineSync;
    public String syncUpload;

    public LatencyRecord(Location _location, String _networkType, String _pingLatency) {
        startTime = new Date().getTime();
        networkType = _networkType;
        pingLatency = _pingLatency;
        latitude = _location.getLatitude();
        longitude = _location.getLongitude();
    }

    public String getTimestamp(long _millis) {
        Date date = new Date(_millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.0Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    /**
     * A bunch of stupid methods to append ms to the end if applicable, otherwise just return the
     * String which was probably an exception the latency tester threw so it cant be parsed.
     */

    public String getSimpleApiLatency() {
        try {
            return Integer.valueOf(simpleApiLatency) + " ms";
        }
        catch (NumberFormatException e) {
            return simpleApiLatency;
        }
    }

    public String getCustomLogicApi() {
        try {
            return Integer.valueOf(customLogicApi) + " ms";
        }
        catch (NumberFormatException e) {
            return customLogicApi;
        }
    }

    public String getOnlineGetContact() {
        try {
            return Integer.valueOf(onlineGetContact) + " ms";
        }
        catch (NumberFormatException e) {
            return onlineGetContact;
        }
    }

    public String getFullOfflineSync() {
        try {
            return Integer.valueOf(fullOfflineSync) + " ms";
        }
        catch (NumberFormatException e) {
            return fullOfflineSync;
        }
    }

    public String getPartialOfflineSync() {
        try {
            return Integer.valueOf(partialOfflineSync) + " ms";
        }
        catch (NumberFormatException e) {
            return partialOfflineSync;
        }
    }

    public String getSyncUpload() {
        try {
            return Integer.valueOf(syncUpload) + " ms";
        }
        catch (NumberFormatException e) {
            return syncUpload;
        }
    }

    @Override
    public String toString() {
        return
                "Start Time: " + getTimestamp(startTime) + "\n" +
                "End Time: " + getTimestamp(endTime) + "\n" +
                "Network Type: " + networkType + "\n" +
                "Latitude: " + latitude + "\n" +
                "Longitude: " + longitude + "\n" +
                "Ping Latency: " + pingLatency + "\n" +
                "Simple API: " + getSimpleApiLatency() + "\n" +
                "Custom Logic API: " + getCustomLogicApi() + "\n" +
                "Online Get Contact: " + getOnlineGetContact() + "\n" +
                "Full Offline Sync Download: " + getFullOfflineSync() + "\n" +
                "Partial Offline Sync Download: " + getPartialOfflineSync() + "\n" +
                "Sync Upload: " + getSyncUpload() + "\n" +
                "\n";
    }

    public String toCsvLine() {
        return getTimestamp(startTime) + "," + getTimestamp(endTime) + "," + networkType + "," +
            latitude + "," + longitude + "," + pingLatency + "," + getSimpleApiLatency() + "," +
            getCustomLogicApi() + "," + getOnlineGetContact() + "," + getFullOfflineSync() + "," +
            getPartialOfflineSync() + "," + getSyncUpload() + "\n";
     }

}
