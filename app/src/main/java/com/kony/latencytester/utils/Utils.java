package com.kony.latencytester.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by dnorvell on 10/30/16.
 */
public class Utils {

    public static ProgressDialog sProgressDialog = null;
    /**
     * Keep track of the individual context for progress dialogs so we
     * can make sure the dialog is associated with the proper activity
     */
    public static Context sProgressDialogContext;

    public static void setOptionsPreference(Context _context, String _key, boolean _value) {
        SharedPreferences prefs = _context.getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(_key, _value);
        editor.apply();
    }

    public static boolean getOptionsPreference(Context _context, String _key) {
        SharedPreferences prefs = _context.getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
        return prefs.getBoolean(_key, false);
    }

    public static void showDialog(Context _context, String _title, String _message, String _positiveText, DialogInterface.OnClickListener _positiveOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);

        builder.setTitle(_title);
        builder.setMessage(_message);

        builder.setPositiveButton(_positiveText, _positiveOnClickListener);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showDialog(Context _context, String _title, String _message, String _positiveText,
                                  DialogInterface.OnClickListener _positiveOnClickListener, String _negativeText,
                                  DialogInterface.OnClickListener _negativeOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);

        builder.setTitle(_title);
        builder.setMessage(_message);

        builder.setPositiveButton(_positiveText, _positiveOnClickListener);
        builder.setNegativeButton(_negativeText, _negativeOnClickListener);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String ping(String _hostnameOrIp) {
        try {
            Process process = Runtime.getRuntime().exec("ping -c 1 " + _hostnameOrIp);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            return output.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ProgressDialog getProgressDialogInstance(Context context) {
        if (sProgressDialog == null || context != sProgressDialogContext) {
            sProgressDialogContext = context;
            return new ProgressDialog(context);
        } else {
            return sProgressDialog;
        }
    }

    public static void showProgressDialog(Context context, String title, String message, boolean _show) {
        sProgressDialog = getProgressDialogInstance(context);

        if(_show) {
            sProgressDialog.setTitle(title);
            sProgressDialog.setMessage(message);
            sProgressDialog.show();
        }

        else {
            sProgressDialog.dismiss();
            sProgressDialogContext = null;
        }

    }

}
