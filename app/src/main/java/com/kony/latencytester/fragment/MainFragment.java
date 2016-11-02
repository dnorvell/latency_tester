package com.kony.latencytester.fragment;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import butterknife.Bind;
import butterknife.OnClick;


public class MainFragment extends BaseFragment implements LatencyTestManager.LatencyTestListener {

    public static final String TAG = "MainFragment";

    public static final int LOG = 0;

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

        // Start the background service
        if (Utils.getOptionsPreference(getActivity(), Constants.RUN_IN_BACKGROUND)) {
            getActivity().startService(new Intent(getActivity(), LatencyService.class));
        }

    }

    @OnClick(R.id.btn_test)
    void onTestClick() {

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
                startActivityForResult(new Intent(getActivity(), LogActivity.class), LOG);
                return true;

        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOG:
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

        LogFile.appendLog(_latencyRecord.toString());

        Utils.showProgressDialog(getActivity(),
                getString(R.string.please_wait),
                getString(R.string.obtaining_results),
                false);

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
