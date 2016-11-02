package com.kony.latencytester.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kony.latencytester.R;
import com.kony.latencytester.fragment.MainFragment;
import com.kony.latencytester.fragment.NavOneFragment;
import com.kony.latencytester.service.LatencyService;
import com.kony.latencytester.utils.Utils;

import butterknife.Bind;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";


    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);

//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            displayFragment(MainFragment.TAG, false);
        }

    }

    @Override
    protected Fragment getHostedFragment(String _fragmentTag) {
        Fragment f = null;
        switch (_fragmentTag) {
            case MainFragment.TAG:
                f = new MainFragment();
                break;
            case NavOneFragment.TAG:
                f = new NavOneFragment();
                break;
        }
        return f;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (mFragmentManager.getBackStackEntryCount() == 0) {
                promptClose();
            } else {
                mFragmentManager.popBackStack();
            }
        }
    }

    public void promptClose() {
        Utils.showDialog(this,
                getString(R.string.close_application),
                getString(R.string.are_you_sure_exit),
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Finish the main activity which will close the app
                        finish();
                    }
                },
                getString(R.string.no),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case LatencyService.CLOSE_ACTION:
                stopService(new Intent(this, LatencyService.class));
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            displayFragment(MainFragment.TAG, false);
        } else if (id == R.id.nav_gallery) {
            displayFragment(NavOneFragment.TAG, false);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
