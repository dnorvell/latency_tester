package com.kony.latencytester.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

import com.kony.latencytester.R;
import com.kony.latencytester.fragment.LogFragment;

/**
 * Created by dnorvell on 10/30/16.
 */
public class LogActivity extends BaseActivity {

    @Override
    protected Fragment getHostedFragment(String _fragmentTag) {
        Fragment f = null;
        switch (_fragmentTag) {
            case LogFragment.TAG:
                f = new LogFragment();
                break;
        }
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_log;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        if (savedInstanceState == null) {
            displayFragment(LogFragment.TAG, false);
        }

    }

}
