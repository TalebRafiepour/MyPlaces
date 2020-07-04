package com.marand.myplaces.ui.placelist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.marand.myplaces.R;
import com.marand.myplaces.model.Item;
import com.marand.myplaces.util.Constants;
import com.marand.myplaces.util.Utils;
import com.marand.myplaces.viewmodel.MyViewModel;
import com.marand.myplaces.viewmodel.MyViewModelFactory;

import java.util.ArrayList;

public class PlaceListActivity extends AppCompatActivity {
    private static final String TAG = PlaceListActivity.class.getSimpleName();
    private PlaceListActivity placeListActivity;
    private Utils utils;
    private int mOffset = 0;
    private boolean isMorePlaces;
    private String mCurrent_latitude, mCurrent_longitude;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 1;
    private static final int WIFI_SETTINGS_REQUEST_CODE = 2;
    private RelativeLayout mLayout_place_list_activity;
    private RecyclerView mPlace_recycler_view;
    private ProgressBar mProgress_bar;
    private PlaceAdapter mPlace_adapter;
    private MyViewModel myViewModel;
    private ArrayList<Item> placeList;

    @NonNull
    private String[] mLocation_permissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palce_list);
        mPlace_recycler_view = findViewById(R.id.place_recycler_view);

        placeListActivity = this;
        utils = new Utils();

        initView();
        initPlaceRecyclerView();
        checkSettings();

        mPlace_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("onScrollStateChanged","on scroll changed");
                if (!recyclerView.canScrollVertically(1) && isMorePlaces) {
                    mOffset = Constants.LIMIT_COUNT + mOffset;
                    isMorePlaces = false;
                    myViewModel.place().removeObservers(placeListActivity);
                    sendRequest();
                }
            }
        });
    }

// -------------------------------------------------------------------------------------------------

    private void initView() {
        mLayout_place_list_activity = findViewById(R.id.layout_place_list_activity);
        mPlace_recycler_view = findViewById(R.id.place_recycler_view);
        mProgress_bar = findViewById(R.id.progress_bar);
    }
    private void initPlaceRecyclerView() {
        mPlace_recycler_view.setLayoutManager(new LinearLayoutManager(placeListActivity));
        mPlace_adapter = new PlaceAdapter();
        mPlace_recycler_view.setAdapter(mPlace_adapter);
    }

    private boolean locationPermissionHasBeenGranted() {
        return ActivityCompat.checkSelfPermission(
                placeListActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(placeListActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }
    private void checkSettings() {
        // Is GPS enabled?
        final boolean gpsEnabled = utils.locationTrackingEnabled(placeListActivity);
        // Is there internet connectivity?
        final boolean internetConnected = utils.internetConnectivity(placeListActivity);

        if (gpsEnabled && internetConnected) {
            requestLocationPermission();
        } else if (!gpsEnabled) {
            final Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            showDialog(gpsIntent, LOCATION_SETTINGS_REQUEST_CODE, getString(R.string.location_tracking_off));
        } else {
            final Intent internetIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            showDialog(internetIntent, WIFI_SETTINGS_REQUEST_CODE, getString(R.string.wireless_off));
        }
    }
    private void requestLocationPermission() {
        if (locationPermissionHasBeenGranted()) {
            // Permission has been granted, launch setup
            getCoordinates();
        } else {
            ActivityCompat.requestPermissions(placeListActivity, mLocation_permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private Location getCurrentLocation() {
        // Make sure we really do have permission before trying to access location.
        if (ActivityCompat.checkSelfPermission(
                placeListActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                placeListActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(placeListActivity, mLocation_permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return null;
        }

        // Get the user's current location.
        LocationManager locationManager = (LocationManager) placeListActivity.getSystemService(LOCATION_SERVICE);
        String locationProvider = getLocationProvider(locationManager);
        if (null == locationProvider) {
            return null;
        }
        return locationManager.getLastKnownLocation(locationProvider);
    }
    private String getLocationProvider(@NonNull LocationManager manager) {
        String locationProvider = null;
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        return locationProvider;
    }
    private void listenForLocationUpdates() {
        final LocationManager locationManager = (LocationManager) placeListActivity.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(placeListActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Don't have permission to listen to the current device location!");
            return;
        }

        locationManager.requestLocationUpdates(getLocationProvider(locationManager),
                60000,
                10,
                new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationManager.removeUpdates(this);
                getCoordinates();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        });
    }
    private void getCoordinates() {
        Location location = getCurrentLocation();
        if (location != null) {
            double lat = location.getLatitude();
            double longi = location.getLongitude();
            mCurrent_latitude = String.valueOf(lat);
            mCurrent_longitude = String.valueOf(longi);
            Log.e(TAG, "got Coordinates: "+ mCurrent_longitude + " // "+mCurrent_latitude);
            sendRequest();
        } else {
            Toast.makeText(placeListActivity, getString(R.string.error_location), Toast.LENGTH_SHORT).show();
            listenForLocationUpdates();
        }
    }

    private void showDialog(final Intent intent, final int requestCode, final String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlaceListActivity.this);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getString(R.string.button_yes), (dialog, which) -> startActivityForResult(intent, requestCode));
        alertDialog.setNegativeButton(getString(R.string.button_no), (dialog, which) -> finish());
        alertDialog.create().show();
    }

    private void sendRequest() {
        String ll = mCurrent_latitude+","+mCurrent_longitude;
        myViewModel = new ViewModelProvider(placeListActivity, new MyViewModelFactory(
                getApplication()
                )).get(MyViewModel.class);

        myViewModel.getPlace(Constants.CLIENT_ID,
                Constants.CLIENT_SECRET,
                Constants.FOURSQUARE_VERSION_NUMBER,
                Constants.LIMIT_COUNT,
                mOffset,
                ll).observe(placeListActivity, placeResource -> {
            if (placeResource != null) {
                switch (placeResource.status) {
                    case SUCCESS: {
                        mProgress_bar.setVisibility(View.GONE);
                        isMorePlaces = Constants.LIMIT_COUNT <= placeResource.data.getResponse().getGroups().get(0).getItems().size();
                        if (mOffset == 0){
                            placeList =  placeResource.data.getResponse().getGroups().get(0).getItems();
                        }else {
                            placeList.addAll( placeResource.data.getResponse().getGroups().get(0).getItems());
                        }

                        mPlace_adapter.setItems(placeList);
                        // This below part is for UI updating!
                        /*Parcelable parcelable = mPlace_recycler_view.getLayoutManager().onSaveInstanceState();
                        mPlace_recycler_view.getLayoutManager().onRestoreInstanceState(parcelable);*/
                        break;
                    }
                    case LOADING: {
                        mProgress_bar.setVisibility(View.VISIBLE);
                        break;
                    }
                    case ERROR: {
                        mProgress_bar.setVisibility(View.GONE);
                        Toast.makeText(placeListActivity, getString(R.string.error_data), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
    }

// -------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WIFI_SETTINGS_REQUEST_CODE || requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            checkSettings();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(placeListActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(mLayout_place_list_activity, getString(R.string.permission_location_required), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.button_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    requestLocationPermission();
                                }
                            }).show();
                }
            } else {
                // Permission has been granted, launch setup
                getCoordinates();
            }
        }
    }

}
