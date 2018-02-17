package com.jyothi.ergast;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.databinding.ActivityMainBinding;
import com.jyothi.ergast.di.MainActivityModule;
import com.jyothi.ergast.util.ActivityUtils;
import com.jyothi.ergast.util.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

    @Inject
    public ItemAdapter mAdapter;

    @Inject
    public LinearLayoutManager mLayoutManager;

    private ActivityMainBinding mBinding;

    private MainViewModel mViewModel;

    private MainActivityComponent mComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mComponent = DaggerMainActivityComponent.builder()
                .mainActivityModule(new MainActivityModule(this))
                .build();
        mComponent.inject(this);

        initializeViewModel();
        initDataBinding();

        // Checking if necessary permission are given by user or not
        checkPermissions();
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mBinding.included.recyclerView.setLayoutManager(mLayoutManager);
        mBinding.included.recyclerView.setAdapter(mAdapter);
        mBinding.included.recyclerView.addOnScrollListener(mScrollListener);
        mBinding.included.recyclerView.setVisibility(View.VISIBLE);

        mBinding.setModel(mViewModel);
    }

    private void checkPermissions() {
        ArrayList<String> allPerms = getAllPermissions();

        if (ActivityUtils.requestPermissions(this, REQUEST_ALL_PERMISSIONS, allPerms)) {
            Log.i(TAG, "Permissions are there querying");
            //Showing progress dialog
            startProgressDialog();
            initializeViewModel();
        }
    }

    private void initializeViewModel() {
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
                    initializeViewModel();
                } else {
                    finish();
                }

                break;
            }
        }
    }

    public void startProgressDialog() {
        mBinding.included.progressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressDialog() {
        mBinding.included.progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
