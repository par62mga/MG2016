package com.pkrobertson.demo.mg2016;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pkrobertson.demo.mg2016.data.AppConfig;


/**
 * MapActivity -- an activity to show user's location and the address of the MG2016 event/hotel.
 *     This was directly taken from the Google Maps API v2 Android Samples Master:
 *     MyLocationDemoActivity with updates to show MG2016 info.
 */
public class MapActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String LOG_TAG            = MapActivity.class.getSimpleName();

    private static final String EXTRA_MAP_TITLE    = "map_title";
    private static final String EXTRA_MAP_SNIPPET  = "map_snippet";
    private static final String EXTRA_MAP_LOCATION = "map_location";

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // information provided by MG2016 when the map is launched
    private String mMapTitle    = null;
    private String mMapSnippet  = null;
    private String mMapLocation = null;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    public static void launchMapActivity (
            Context context, String mapTitle, String mapSnippet, String mapLocation) {
        Intent mapIntent = new Intent(context, MapActivity.class);
        mapIntent.putExtra (EXTRA_MAP_TITLE, mapTitle);
        mapIntent.putExtra (EXTRA_MAP_SNIPPET, mapSnippet);
        mapIntent.putExtra (EXTRA_MAP_LOCATION, mapLocation);
        context.startActivity(mapIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMapTitle    = extras.getString (EXTRA_MAP_TITLE);
            mMapSnippet  = extras.getString (EXTRA_MAP_SNIPPET);
            mMapLocation = extras.getString (EXTRA_MAP_LOCATION);
        }

        // show default location if either of the extras were not found
        if (mMapTitle == null || mMapLocation == null) {
            AppConfig appConfig = AppConfig.getInstance(this);
            mMapTitle    = appConfig.getDefaultLocation();
            mMapSnippet  = "";
            mMapLocation = appConfig.getDefaultMap();
        }
        ActionBar actionBar = getSupportActionBar();

        // show title of location
        actionBar.setTitle(mMapTitle);
        actionBar.setSubtitle(R.string.app_name);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // treat home as back press to return to same screen in the MainActivity
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        enableMyLocation();

        // make sure we got a lat,long map location as expected.
        int foundComma = mMapLocation.indexOf (',');
        if (foundComma > 0) {
            double lat = Double.parseDouble(mMapLocation.substring(0, foundComma));
            double lng = Double.parseDouble(mMapLocation.substring(foundComma + 1));
            Log.d(LOG_TAG,
                    "onMapReady() location ==> " + mMapLocation +
                            " lat ==> " + String.valueOf(lat) +
                            " lng ==> " + String.valueOf(lng));

            final LatLng location = new LatLng(lat, lng);

            // add marker to location based on title and snippet
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(mMapTitle)
                    .snippet(mMapSnippet));

            // Move the camera instantly to location with a zoom of 15.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 7));

            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

            // this let's them select navigate and other options
            marker.showInfoWindow();
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}
