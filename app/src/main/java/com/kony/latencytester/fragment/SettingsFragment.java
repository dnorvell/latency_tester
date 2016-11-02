package com.kony.latencytester.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.kony.latencytester.R;
import com.kony.latencytester.service.LatencyService;
import com.kony.latencytester.utils.Constants;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by dnorvell on 10/30/16.
 */
public class SettingsFragment extends BaseFragment {

    public static final String TAG = "SettingsFragment";

    @Bind(R.id.sw_run_background)
    Switch mSwRunBackground;

    @Bind(R.id.sw_show_snack)
    Switch mSwShowSnack;

    @Bind(R.id.et_background_interval)
    EditText mEtBackgroundInterval;

    @Bind(R.id.et_api_timout)
    EditText mEtApiTimeout;

    @Bind(R.id.et_ping_host)
    EditText mEtPingHost;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initUi(View _rootView, Bundle _savedInstanceState) {
        super.initUi(_rootView, _savedInstanceState);
        loadSettings();
    }

    private void loadSettings() {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
        mSwRunBackground.setChecked(prefs.getBoolean(Constants.RUN_IN_BACKGROUND, true));
        mSwShowSnack.setChecked(prefs.getBoolean(Constants.SHOW_SNACKBAR, true));
        mEtBackgroundInterval.setText(String.valueOf(prefs.getInt(Constants.BACKGROUND_INTERVAL, 30)));
        mEtApiTimeout.setText(String.valueOf(prefs.getInt(Constants.API_TIMEOUT, 300)));
        mEtPingHost.setText(prefs.getString(Constants.PING_HOST, "8.8.8.8"));
    }

    @OnClick(R.id.btn_save)
    void onSaveClick() {
        saveSettings();
    }

    private void saveSettings() {

        if(mSwRunBackground.isChecked()) {
            getActivity().startService(new Intent(getActivity(), LatencyService.class));
        }
        else {
            getActivity().stopService(new Intent(getActivity(), LatencyService.class));
        }

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.RUN_IN_BACKGROUND, mSwRunBackground.isChecked());
        editor.putBoolean(Constants.SHOW_SNACKBAR, mSwShowSnack.isChecked());
        editor.putInt(Constants.BACKGROUND_INTERVAL, Integer.valueOf(mEtBackgroundInterval.getText().toString()));
        editor.putInt(Constants.API_TIMEOUT, Integer.valueOf(mEtApiTimeout.getText().toString()));
        editor.putString(Constants.PING_HOST, mEtPingHost.getText().toString());
        editor.apply();

        getActivity().finish();
    }

}
