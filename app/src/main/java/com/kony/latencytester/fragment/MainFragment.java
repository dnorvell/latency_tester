package com.kony.latencytester.fragment;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.kony.latencytester.R;
import com.kony.latencytester.activity.LogActivity;
import com.kony.latencytester.activity.SettingsActivity;
import com.kony.latencytester.entities.LatencyRecord;
import com.kony.latencytester.service.LatencyService;
import com.kony.latencytester.utils.Constants;
import com.kony.latencytester.utils.LatencyTestManager;
import com.kony.latencytester.utils.LogFile;
import com.kony.latencytester.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;


public class MainFragment extends BaseFragment implements LatencyTestManager.LatencyTestListener {

    public static final String TAG = "MainFragment";

    public static final int LOG_ACTIVITY_RESULT = 0;

    private static final int PERMISSION_CHECK_READ_STORAGE    = 1;
    private static final int PERMISSION_CHECK_WRITE_STORAGE   = 2;
    private static final int PERMISSION_CHECK_FINE_LOCATION   = 3;
    private static final int PERMISSION_CHECK_COARSE_LOCATION = 4;

    @Bind(R.id.tv_test_results)
    TextView mTvTestResults;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LatencyTestManager.addLatencyTestListener(this);
        setHasOptionsMenu(true);

        checkPermissions();

        // Start the background service
        if (Utils.getOptionsPreference(getActivity(), Constants.RUN_IN_BACKGROUND)) {
            getActivity().startService(new Intent(getActivity(), LatencyService.class));
        }

    }

    @OnClick(R.id.btn_manual_test)
    void onManualTestClick() {
            try {
                Utils.showProgressDialog(getActivity(),
                        getString(R.string.please_wait),
                        getString(R.string.obtaining_results),
                        true);

                LatencyTestManager.getInstance(getActivity()).start();
            }
            catch (NetworkErrorException e) {
                Utils.showDialog(getActivity(),
                        getString(R.string.network_error),
                        getString(R.string.network_error_help) + e.getLocalizedMessage(),
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
            catch (SecurityException e) {
                Utils.showDialog(getActivity(),
                        getString(R.string.security_error),
                        getString(R.string.security_error_help) + e.getLocalizedMessage(),
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;

            case R.id.action_view_log:
                startActivityForResult(new Intent(getActivity(), LogActivity.class), LOG_ACTIVITY_RESULT);
                return true;

        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOG_ACTIVITY_RESULT:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // Log was cleared so lets clear the text
                        mTvTestResults.setText("");
                        break;
                    case Activity.RESULT_CANCELED:
                        // Do nothing
                        break;
                }
        }
    }

    @Override
    public void onLatencyTestComplete(LatencyRecord _latencyRecord) {

        if (shouldShowSnackbar()) {
            if (getView() != null) {
                Snackbar.make(getView(), "Results updated", Snackbar.LENGTH_SHORT).show();
            }
        }

        Log.v(TAG, String.valueOf(Thread.currentThread().getId()));

        mTvTestResults.setText(_latencyRecord.toString());

        // If the background service is running, it will also be listening and take care of the logging
        // so dont bother.
        if (!LatencyService.isRunning()) {
            LogFile.appendLog(_latencyRecord.toString());
        }

        Utils.showProgressDialog(getActivity(),
                getString(R.string.please_wait),
                getString(R.string.obtaining_results),
                false);

    }

    private void checkPermissions() {
        Map<Integer, String> requiredPermissionMap = new HashMap<>();
        requiredPermissionMap.put(PERMISSION_CHECK_READ_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        requiredPermissionMap.put(PERMISSION_CHECK_WRITE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredPermissionMap.put(PERMISSION_CHECK_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        requiredPermissionMap.put(PERMISSION_CHECK_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);

        for (int key : requiredPermissionMap.keySet()) {
            String currentPermission = requiredPermissionMap.get(key);
            checkAndRequestPermissionIfNeeded(currentPermission, key);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            // Normally you'd have a case for each code, but I want to do the same thing for each one.
            default:
                if (permissionGranted(grantResults)) {
                    // Great we'll let them stay
                }
                else {
                    showSnarkyPermissionsDialog();
                }
                break;
        }
    }

    private void showSnarkyPermissionsDialog() {
        Utils.showDialog(getActivity(),
                getString(R.string.user_error),
                getString(R.string.permission_required),
                getString(R.string.ill_leave),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startInstalledAppDetailsActivity(getActivity());
                        getActivity().finish();
                    }
                });
    }

    /**
     * Get the user as close to app settings as possible for permission enabling reasons.
     */
    public static void startInstalledAppDetailsActivity(Context context) {

        if (context == null) {
            return;
        }

        Intent i = new Intent()
                .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setData(Uri.parse("package:" + context.getPackageName()))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(i);

    }

    private boolean shouldShowSnackbar() {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
        return prefs.getBoolean(Constants.SHOW_SNACKBAR, true);
    }

    @Override
    public void onDestroy() {
        LatencyTestManager.removeLatencyTestListener(this);
        super.onDestroy();
    }

}
