package com.kony.latencytester.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by dnorvell on 10/30/16.
 */
public class LogFile {

    public static String TAG = "LogFile";

    public static void appendLog(String text)
    {
        File logFile = new File(getPath());
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String readLog() {

        if(exists()) {
            StringBuilder text = new StringBuilder();
            try {
                File file = new File(getPath());

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
//                    if (line.isEmpty()) {
//                        text.append('\n');
//                    }
                    text.append(line + '\n');
                }
                br.close();
                return text.toString();
            }
            catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
                return null;
            }
        }

        return null;
    }

    public static boolean delete() {
        File file = new File(getPath());
        return file.delete();
    }

    public static boolean exists() {
        File logFile = new File(getPath());
        return logFile.exists();
    }

    public static String getPath() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constants.LOG_FILE_NAME).getPath();
    }

}
