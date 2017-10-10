package com.jyothi.ergast;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.model.MainViewModel;
import com.jyothi.ergast.util.ActivityUtils;
import com.jyothi.ergast.util.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jyothi on 7/22/16.
 */

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_ALL_PERMISSIONS = 112;

    private int mPastItems, mVisibleCount, mTotalCount;
    private boolean mLoading = true;
    private boolean mSearchIsOn = false;
    private boolean mEndOfDrivers = false;

    private ItemAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Driver> mFilterDrivers = null;

    private MainViewModel mViewModel;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mLayoutManager = new LinearLayoutManager(this);
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //Creating the adapter to show the items.
        mAdapter = new ItemAdapter(new ArrayList<Driver>());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setVisibility(View.VISIBLE);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Checking if necessary permission are given by user or not
        checkPermissions();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        mViewModel.getDrivers();
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

        mViewModel.getDrivers().observe(this, new Observer<List<Driver>>() {
            @Override
            public void onChanged(@Nullable List<Driver> drivers) {
                if (drivers != null) {
                    mAdapter.setItems(drivers);
                } else {
                    mAdapter.clearItems();
                }

                stopProgressDialog();
            }
        });

        mViewModel.getQueryDone().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean queryDone) {
                mLoading = queryDone;
            }
        });

        mViewModel.getEndOfDrivers().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean endOfDrivers) {
                mEndOfDrivers = endOfDrivers;
            }
        });

        mViewModel.getShowProgress().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean showProgress) {
                if (showProgress) {
                    startProgressDialog();
                } else {
                    stopProgressDialog();
                }
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

        if (!mLoading || mSearchIsOn) {
            return;
        }

        if ((mVisibleCount + mPastItems) >=
                (mTotalCount - ((Utils.MIN_PAGES_CONSTANT - 1) * Utils.ITEMS_PER_PAGE_CONSTANT))) {
            if (mEndOfDrivers) {
                return;
            }

            getNextPageItems();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getNextPageItems() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, R.string.wifi_or_data_not_enabled, Toast.LENGTH_SHORT).show();
            mLoading = true;
            return;
        } else {
            mLoading = false;
        }

        mViewModel.loadNextSetUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView((getSupportActionBar().getThemedContext()));
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        searchItem.setActionView(searchView);

        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.enter_driver_id));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                mViewModel.refresh();
                mAdapter.clearItems();
                mViewModel.loadDrivers();
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
        mAdapter.getFilter().filter(newText);

        if (newText.equals("")) {
            mSearchIsOn = false;
        } else {
            mSearchIsOn = true;
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
        allPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        allPermissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        allPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

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
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
