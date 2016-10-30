package com.kony.latencytester.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kony.latencytester.R;
import com.kony.latencytester.application.LatencytesterApplication;
import com.kony.latencytester.utils.Constants;
import com.kony.latencytester.web.WebApi;

import butterknife.ButterKnife;


/**
 * Base Activity responsible for interfacing with Dagger and {@link ButterKnife}. All activities
 * within the application should extend from this one. Doing so will ensure that they are setup for
 * dependency injection from Dagger if needed, and also sets them up to manage the Butterknife life
 * cycle.
 * <p/>
 * Additionally activities who extend from this one will have access to the
 * {@link #runInBackground(Runnable)} (Runnable)} and {@link #runInBackgroundDelayed(Runnable, long)}
 * (Runnable)} methods which can simply threading needs.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";

    public LatencytesterApplication mApplication;
    protected Fragment mCurrentFragment;
    /**
     * A handler that can be used to perform actions on the thread for this activity. To post to the
     * handler, first get a reference to it via {@link #getHandler()}, and then use the
     * {@link #runInBackground(Runnable)} or {@link #runInBackgroundDelayed(Runnable, long)} methods
     * as desired. See {@link #mHandlerThread}. This variable is cleaned up in {@link #onDestroy()}
     */
    protected Handler mHandler;
    FragmentManager mFragmentManager = getSupportFragmentManager();
    /**
     * A handler thread that will be spawned and maintained by this class when {@link #getHandler()}
     * is called. When using {@link #runInBackground(Runnable)} or
     * {@link #runInBackgroundDelayed(Runnable, long)} methods, runnables will be posted on
     * {@link #mHandler} and queued up to be executed on this thread.
     * * This variable is cleaned up in {@link #onDestroy()}
     */
    private HandlerThread mHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

        if (savedInstanceState != null && savedInstanceState.getString("CurrentFragmentTag") != null) {
            mCurrentFragment = mFragmentManager.findFragmentByTag(savedInstanceState.getString("CurrentFragmentTag"));
        }

        if (Constants.SHOW_LIFE_CYCLE_CHANGES)
            Log.v(getClass().getSimpleName(), "Activity Created");

        // Perform injection so that when this call returns all dependencies will be available for use.
        mApplication = (LatencytesterApplication) getApplication();

        //TODO Strict mode is useful for seeing where threading might be needed. Remove when ready.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        //Removes strict mode multiple instances of activity violation.
        //See: http://stackoverflow.com/questions/21145261/strictmode-activity-instance-count-violation-2-instances-1-expected-on-rotati
        System.gc();

    }

    /**
     * Fragments that this activity can host should be returned here.
     * If they are not created/returned by this method then they will be unable to be displayed.
     *
     * @param _fragmentTag A unique that that identifies the fragment. The fragment that is returned will be according to this tag
     * @return An instance of the fragment that corresponds to the given tag.
     */
    protected abstract Fragment getHostedFragment(String _fragmentTag);

    public void displayFragment(String _fragmentTag, Bundle _bundle, boolean _addToBackStack) {
        Fragment newFragment = getHostedFragment(_fragmentTag);

        if (newFragment != null) {
            if (_bundle != null) {
                newFragment.setArguments(_bundle);
            }

            //Stops duplicates
            if (mCurrentFragment != null && mCurrentFragment.getTag() != null && mCurrentFragment.getTag().equals(_fragmentTag)) {
                return;
            }

            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            //Set the currently displayed fragment
            mCurrentFragment = newFragment;

            if (_addToBackStack) {
                mFragmentTransaction.addToBackStack(null);
            }

            //Transition to the next fragment
            mFragmentTransaction.replace(R.id.fl_content, newFragment, _fragmentTag).commit();
        }
    }

    public void displayFragment(String _fragmentTag, boolean _addToBackStack) {
        displayFragment(_fragmentTag, null, _addToBackStack);
    }

    /**
     * Gets the layout id that should be used to set the content view for this activity.
     *
     * @return The layout id used for this activity.
     */
    protected abstract int getLayoutId();

    /**
     * Gets a handler object for the purpose of posting runnables on the thread. There should
     * only ever be one handlerthread/handler object when getting them via this method. They are
     * cleaned up in {@link #onDestroy()}. Any action that is executing when the activity is
     * destroyed will be quit.
     *
     * @return mHandler
     */
    private Handler getHandler() {
        if (mHandler == null) {
            if (mHandlerThread == null) {
                mHandlerThread = new HandlerThread(getClass().getName());
                mHandlerThread.start();
            }
            mHandler = new Handler(mHandlerThread.getLooper());
        }
        return mHandler;
    }

    /**
     * Automatically runs the desired runnable on a background thread.
     *
     * @param _runnable a Runnable to be run.
     */
    protected void runInBackground(Runnable _runnable) {
        getHandler().post(_runnable);
    }

    /**
     * Automatically runs the desired runnable on a background thread after the specified delay.
     *
     * @param _runnable    a Runnable to run.
     * @param _delayMillis the delay in millis that will pass before the runnable is posted to the
     *                     handler.
     */
    protected void runInBackgroundDelayed(Runnable _runnable, long _delayMillis) {
        getHandler().postDelayed(_runnable, _delayMillis);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentFragment != null && mCurrentFragment.getTag() != null) {
            outState.putString("CurrentFragmentTag", mCurrentFragment.getTag());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Constants.SHOW_LIFE_CYCLE_CHANGES) Log.v(getClass().getSimpleName(), "Activity Paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.SHOW_LIFE_CYCLE_CHANGES)
            Log.v(getClass().getSimpleName(), "Activity Resumed");
    }

    /**
     * Make sure to clean up threading components.
     */
    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.getLooper().quitSafely();
        }
        mHandler = null;
        mHandlerThread = null;

        super.onDestroy();
        if (Constants.SHOW_LIFE_CYCLE_CHANGES)
            Log.v(getClass().getSimpleName(), "Activity Destroyed");
    }
}
