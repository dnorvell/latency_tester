package com.kony.latencytester.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kony.latencytester.R;
import com.kony.latencytester.fragment.LogFragment;

import butterknife.Bind;

/**
 * Created by dnorvell on 10/30/16.
 */
public class LogActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

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
        return R.layout.app_bar_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState == null) {
            displayFragment(LogFragment.TAG, false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

}
