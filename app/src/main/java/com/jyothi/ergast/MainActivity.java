package com.jyothi.ergast;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toolbar;

import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.model.MainViewModel;
import com.jyothi.ergast.util.ActivityUtils;

import java.util.ArrayList;

/**
 * Created by Jyothi on 7/22/16.
 */

public class MainActivity extends LifecycleActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_ALL_PERMISSIONS = 112;

    private int mPastItems, mVisibleCount, mTotalCount;
    private boolean mLoading = true;
    private boolean mSearchIsOn = false;

    private ItemAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private MainViewModel mViewModel;
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setTitle(R.string.app_name);

        mLayoutManager = new LinearLayoutManager(this);
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //Creating the adapter to show the items.
        mAdapter = new ItemAdapter(new ArrayList<Driver>());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setVisibility(View.VISIBLE);

        // Checking if necessary permission are given by user or not
        checkPermissions();
    }

    private void checkPermissions() {
        ArrayList<String> allPerms = getAllPermissions();

        if (ActivityUtils.requestPermissions(this, REQUEST_ALL_PERMISSIONS, allPerms)) {
            Log.i(TAG, "Permissions are there querying");
            //Showing progress dialog
            startProgressDialog();
            createViewModel();
        }
    }

    private void createViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getDrivers().observe(this, drivers -> {
            if (drivers != null) {
                mAdapter.setItems(drivers);
            } else {
                mAdapter.clearItems();
            }

            stopProgressDialog();
        });

        mViewModel.getQueryDone().observe(this, queryDone -> {
            mLoading = queryDone;
        });

        mViewModel.getShowProgress().observe(this, showProgress -> {
            if (showProgress) {
                startProgressDialog();
            } else {
                stopProgressDialog();
            }
        });
    }

    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0) {
                checkScrolledDown();
            }
        }
    };

    public void checkScrolledDown() {
        mVisibleCount = mLayoutManager.getChildCount();
        mTotalCount = mLayoutManager.getItemCount();
        mPastItems = mLayoutManager.findFirstVisibleItemPosition();

        if (!mLoading) {
            return;
        }

        if ((mVisibleCount + mPastItems) >= mTotalCount) {
            mLoading = false;

            getNetPageItems();
        }
    }

    public void getNetPageItems() {
        mViewModel.loadNextSetUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView((getActionBar().getThemedContext()));
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        searchItem.setActionView(searchView);

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                mViewModel.refresh();
                mAdapter.clearItems();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.equals("")) {
            mViewModel.clearDrivers();
            mViewModel.loadUsers(false, true);
            mSearchIsOn = false;
        } else {
            mSearchIsOn = true;
            mViewModel.getDriverWithDriverId(newText);
        }

        return false;
    }

    /**
     * Creating a list of permissions needed for the application, to seek user
     * permission.
     *
     * @return list
     */
    public ArrayList<String> getAllPermissions() {
        ArrayList<String> allPermissions = new ArrayList<String>();
        allPermissions.add(Manifest.permission.INTERNET);

        return allPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ALL_PERMISSIONS: {
                boolean grant = true;
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        grant = false;
                    }
                }

                if (grant) {
                    createViewModel();
                } else {
                    finish();
                }

                break;
            }
        }
    }

    public void startProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }

        mProgressDialog = mProgressDialog.show(this, getString(R.string.progress_title),
                getString(R.string.progress_message), false, true);
    }

    public void stopProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        stopProgressDialog();

        super.onDestroy();
    }
}