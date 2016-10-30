package com.kony.latencytester.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

import com.kony.latencytester.R;
import com.kony.latencytester.fragment.SettingsFragment;

/**
 * Created by dnorvell on 10/30/16.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected Fragment getHostedFragment(String _fragmentTag) {
        Fragment f = null;
        switch (_fragmentTag) {
            case SettingsFragment.TAG:
                f = new SettingsFragment();
                break;
        }
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        if (savedInstanceState == null) {
            displayFragment(SettingsFragment.TAG, false);
        }

    }

}
