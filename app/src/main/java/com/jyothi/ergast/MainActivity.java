package com.jyothi.ergast;

import android.Manifest;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jyothi.ergast.adapter.DriverAdapter;
import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.databinding.ActivityMainBinding;
import com.jyothi.ergast.util.ActivityUtils;
import com.jyothi.ergast.viewmodel.MainViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by Jyothi on 7/22/16.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_ALL_PERMISSIONS = 112;

    @Inject
    public DriverAdapter mAdapter;

    @Inject
    public ViewModelProvider.Factory mViewModelFactory;

    private ActivityMainBinding mBinding;
    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        initializeViewModel();
        initDataBinding();

        // Checking if necessary permission are given by user or not
        checkPermissions();
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mBinding.included.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.included.recyclerView.setAdapter(mAdapter);
        mBinding.included.recyclerView.setVisibility(View.VISIBLE);

        mBinding.setModel(mViewModel);
    }

    private void checkPermissions() {
        ArrayList<String> allPerms = getAllPermissions();

        if (ActivityUtils.requestPermissions(this, REQUEST_ALL_PERMISSIONS, allPerms)) {
            Log.i(TAG, "Permissions are there querying");
            initializeViewModel();
        }
    }

    private void initializeViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainViewModel.class);

        initializeDrivers();
    }

    private void initializeDrivers() {
        mViewModel.getDrivers().observe(this, new Observer<PagedList<Driver>>() {
            @Override
            public void onChanged(@Nullable PagedList<Driver> drivers) {
                if (drivers != null) {
                    mAdapter.submitList(drivers);
                }
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        final MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = new SearchView((getSupportActionBar().getThemedContext()));
//        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        searchItem.setActionView(searchView);
//        searchView.setOnQueryTextListener(this);
//        searchView.setQueryHint(getString(R.string.enter_driver_id));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                mViewModel.removeDriversObserver(this);
                mViewModel.refresh();
                resetAdapter(mBinding.included.recyclerView);
                initializeDrivers();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return true;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        mViewModel.clearFilteredList(this);
//        resetAdapter(mBinding.included.recyclerView);
//
//        if (TextUtils.isEmpty(newText)) {
//            initializeDrivers();
//        } else {
//            mViewModel.removeDriversObserver(this);
//            mViewModel.setFilter(newText);
//            initializeFilteredDrivers();
//        }
//
//        return true;
//    }

    public void resetAdapter(RecyclerView view) {
        view.setAdapter(null);
        view.setLayoutManager(null);

        mAdapter = new DriverAdapter();

        view.setAdapter(mAdapter);
        view.setLayoutManager(new LinearLayoutManager(this));
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

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
