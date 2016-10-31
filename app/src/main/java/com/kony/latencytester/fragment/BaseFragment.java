package com.kony.latencytester.fragment;


import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kony.latencytester.activity.BaseActivity;
import com.kony.latencytester.utils.Constants;

import butterknife.ButterKnife;


/**
 * Base fragment responsible for interfacing with Dagger and {@link ButterKnife}. All fragments
 * within the application should extend from this one. Doing so will ensure that they are setup for
 * dependency injection from Dagger if needed, and also sets them up to manage the Butterknife life
 * cycle. Additionally fragments who extend from this one will have access to the
 * {@link #runInBackground(Runnable)} and {@link #runInBackgroundDelayed(Runnable, long)} methods
 * which can simply threading needs.
 */
public abstract class BaseFragment extends DialogFragment {

    public static final String TAG = "BaseFragment";

    /**
     * A handler that can be used to perform actions on the thread for this fragment. To post to the
     * handler, first get a reference to it via {@link #getHandler()}, and then use the
     * {@link #runInBackground(Runnable)} or {@link #runInBackgroundDelayed(Runnable, long)} methods
     * as desired. See {@link #mHandlerThread}. This variable is cleaned up in {@link #onDestroy()}
     */
    protected Handler mHandler;

    /**
     * A handler thread that will be spawned and maintained by this class when {@link #getHandler()}
     * is called. When using {@link #runInBackground(Runnable)} or
     * {@link #runInBackgroundDelayed(Runnable, long)} methods, runnables will be posted on
     * {@link #mHandler} and queued up to be executed on this thread.
     * * This variable is cleaned up in {@link #onDestroy()}
     */
    private HandlerThread mHandlerThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (Constants.SHOW_LIFE_CYCLE_CHANGES)
            Log.v(getClass().getSimpleName(), "Fragment Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Persist state if already added
        if (!isAdded()) {
            return null;
        }

        View v = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, v);

        initUi(v, savedInstanceState);

        return v;
    }

    /**
     * Fragments extending this one who wish to modify the UI after all views have been bound may do
     * so here. This can sometimes be useful to ensure certain operations occur AFTER all
     * {@link ButterKnife} elements have been bound through {@link ButterKnife#bind(Object, View)}.
     */
    protected void initUi(View _rootView, Bundle _savedInstanceState) {

    }

    /**
     * Gets the layout id that should be inflated for this fragment.
     *
     * @return The layout id used for this fragment.
     */
    protected abstract int getLayoutId();

    /**
     * Convenience method for checking if this fragment has a bundle.
     *
     * @return True if it does, False otherwise.
     */
    protected boolean checkBundle() {
        Bundle b = getArguments();
        return b != null;
    }

    /**
     * Convenience method for providing a {@link BaseActivity} object should its methods be needed.
     *
     * @return {@link BaseActivity}
     */
    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    /**
     * Gets a handler object for the purpose of posting runnables on the thread. There should
     * only ever be one handlerthread/handler object when getting them via this method. They are
     * cleaned up in {@link #onDestroy()}. Any action that is executing when the fragment is
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

    /**
     * Make sure that {@link ButterKnife} unbinds all views when this fragment is no longer active.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Constants.SHOW_LIFE_CYCLE_CHANGES) Log.v(getClass().getSimpleName(), "Fragment Paused");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.SHOW_LIFE_CYCLE_CHANGES)
            Log.v(getClass().getSimpleName(), "Fragment Resumed");
    }

    /**
     * Make sure to clean up threading components.
     */
    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.getLooper().quitSafely();
        }
        mHandler = null;
        mHandlerThread = null;

        if (Constants.SHOW_LIFE_CYCLE_CHANGES)
            Log.v(getClass().getSimpleName(), "Fragment Destroyed");
        super.onDestroy();
    }

}
