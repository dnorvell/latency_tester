package com.kony.latencytester.utils;


public class Constants {

    /**
     * Logs life cycle events for fragments and activities who extend from BaseFragment/BaseActivity
     * This can be useful for debugging purposes.
     */
    public static final boolean SHOW_LIFE_CYCLE_CHANGES = false;

    /**
     * The base url for any web calls, used in retrofit.
     */
    public static final String BASE_URL = "https://apps.konycloud.com/services/";

    /**
     * Shared Preferences File.
     */
    public static final String PREFS_FILE = "latency_prefs";

    /**
     * The name of the log file generated during testing.
     */
    public static final String LOG_FILE_NAME = "latency_test.txt";

    /**
     * Shared preferences key for whether or not latency service should be running.
     */
    public static final String RUN_IN_BACKGROUND = "run_in_background";

}
