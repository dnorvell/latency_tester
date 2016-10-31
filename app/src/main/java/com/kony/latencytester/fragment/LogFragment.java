package com.kony.latencytester.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kony.latencytester.R;
import com.kony.latencytester.utils.LogFile;
import com.kony.latencytester.utils.Utils;

import java.io.File;

import butterknife.Bind;

/**
 * Created by dnorvell on 10/30/16.
 */
public class LogFragment extends BaseFragment {

    public static final String TAG = "LogFragment";

    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;
    private Uri mShareUri;

    @Bind(R.id.tv_log_contents)
    TextView mTvLogContents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivity().setTitle(getString(R.string.log_viewer));
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_log;
    }

    @Override
    protected void initUi(View _rootView, Bundle _savedInstanceState) {
        super.initUi(_rootView, _savedInstanceState);

        if (LogFile.exists()) {
            mTvLogContents.setText(LogFile.readLog());
        }
        else {
            mTvLogContents.setText(getString(R.string.no_log));
        }

    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        if (LogFile.exists()) {
//            MenuItem shareItem = menu.findItem(R.id.menu_share);
//            ShareActionProvider shareAction = new ShareActionProvider(getActivity());
//            File logFile = new File(LogFile.getPath());
//            shareAction.setShareIntent(createShareIntent(Uri.fromFile(logFile)));
//        }
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (LogFile.exists()) {
            inflater.inflate(R.menu.fragment_log, menu);

            MenuItem shareItem = menu.findItem(R.id.menu_share);
            ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

            if (provider != null) {
                File logFile = new File(LogFile.getPath());
                provider.setShareIntent(createShareIntent(Uri.fromFile(logFile)));
            }

//            MenuItem shareItem = menu.findItem(R.id.menu_share);
//            shareItem.getActionProvider()
//
//            ShareActionProvider shareActionProvider = new ShareActionProvider(getActivity());
//            shareActionProvider.setOnShareTargetSelectedListener(this);
//
//            // Attach an intent to this ShareActionProvider.  You can update this at any time,
//            // like when the user selects a new piece of data they might like to share.
//            File logFile = new File(LogFile.getPath());
//            shareActionProvider.setShareIntent(createShareIntent(Uri.fromFile(logFile)));
//
//            MenuItemCompat.setActionProvider(shareItem, shareActionProvider);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                Utils.showDialog(getActivity(),
                        getString(R.string.delete),
                        getString(R.string.delete_check),
                        getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogFile.delete();
                                dialog.dismiss();
                                getActivity().setResult(Activity.RESULT_OK);
                                getActivity().finish();
                            }
                        },
                        getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent(Uri _fileUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, _fileUri);
        startActivity(Intent.createChooser(shareIntent, "Share using..."));
        return shareIntent;
    }

}
