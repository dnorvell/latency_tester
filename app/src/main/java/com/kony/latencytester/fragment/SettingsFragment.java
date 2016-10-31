package com.kony.latencytester.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kony.latencytester.R;

/**
 * Created by dnorvell on 10/30/16.
 */
public class SettingsFragment extends BaseFragment {

    public static final String TAG = "SettingsFragment";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initUi(View _rootView, Bundle _savedInstanceState) {
        super.initUi(_rootView, _savedInstanceState);
    }
}
