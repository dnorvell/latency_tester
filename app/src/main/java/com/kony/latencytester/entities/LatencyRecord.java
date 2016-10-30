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
    public int pingLatency;
    public String simpleApiLatency;
    public String customLogicApi;
    public String onlineGetContact;
    public String fullOfflineSync;
    public String partialOfflineSync;
    public String syncUpload;

    public LatencyRecord(Location _location, String _networkType, int _pingLatency) {
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

    @Override
    public String toString() {
        return
                "Start Time: " + getTimestamp(startTime) + "\n" +
                "End Time: " + getTimestamp(endTime) + "\n" +
                "Network Type: " + networkType + "\n" +
                "Latitude: " + latitude + "\n" +
                "Longitude: " + longitude + "\n" +
                "Ping Latency: " + pingLatency + "\n" +
                "Simple API: " + simpleApiLatency + "\n" +
                "Custom Logic API: " + customLogicApi + "\n" +
                "Online Get Contact: " + onlineGetContact + "\n" +
                "Full Offline Sync Download: " + fullOfflineSync + "\n" +
                "Partial Offline Sync Download: " + partialOfflineSync + "\n" +
                "Sync Upload: " + syncUpload + "\n" +
                "\n";
    }
}
