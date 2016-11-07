package com.agencyemr.milerocker.gpstracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.agencyemr.milerocker.gpstracker.maputils.LatLngInterpolator;
import com.agencyemr.milerocker.gpstracker.maputils.MarkerAnimation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    boolean animated = false;
    private GoogleMap mMap;
    private Marker marker;
    private boolean isAllPermissionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ((App) getApplication()).setCurrentActivity(this);

        EventBus.getDefault().register(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // if(AppUtils.isLocationEnabled(this))
        showPermissionsDialog();
    }


    private void initService() {
        startService(new Intent(this, LocationUpdateService.class));
    }

    public void showPermissionsDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWriteExternalStoragePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadExternalStoragePermission = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasAccessFineLocationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            int hasAccessCoarseLocationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasWriteGetAccountsPermission = checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS);
            int hasCallPhonePermission = checkSelfPermission(android.Manifest.permission.CALL_PHONE);
            int hasReadContactsPermission = checkSelfPermission(android.Manifest.permission.READ_CONTACTS);
            int hasReadPhoneStatePermission = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);

            if (
                //hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED
                // || hasWriteGetAccountsPermission != PackageManager.PERMISSION_GRANTED
                    hasAccessFineLocationPermission != PackageManager.PERMISSION_GRANTED
                            || hasAccessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED
                            || hasReadPhoneStatePermission != PackageManager.PERMISSION_GRANTED
                // || hasCallPhonePermission != PackageManager.PERMISSION_GRANTED
                // || hasReadContactsPermission != PackageManager.PERMISSION_GRANTED
                // || hasReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED
                    ) {
                requestPermissions(new String[]{
                        //android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        //  , android.Manifest.permission.GET_ACCOUNTS
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                        , android.Manifest.permission.ACCESS_COARSE_LOCATION
                        , android.Manifest.permission.READ_PHONE_STATE
                        // , android.Manifest.permission.CALL_PHONE
                        //  , android.Manifest.permission.READ_CONTACTS
                        // , android.Manifest.permission.READ_EXTERNAL_STORAGE
                }, REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                isAllPermissionsGranted = true;

                initService();
            }
        } else {
            isAllPermissionsGranted = true;

            initService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0) {
                boolean isAllPermissionsGranted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isAllPermissionsGranted = false;
                    }
                }

                if (isAllPermissionsGranted) {
                    this.isAllPermissionsGranted = true;

                    initService();
                } else {
                    android.widget.Toast.makeText(this, "Please, grand permissions", android.widget.Toast.LENGTH_LONG).show();
                    showPermissionsDialog();
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChangedEvent(Location location) {
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());

        addMarker(location);
    }

    private void addMarker(Location location) {
        if (location != null) {
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

//            Marker myOldLocationMarker=null;
//            if (myLocationMarker != null){
//                myOldLocationMarker = this.myLocationMarker;
//                this.myLocationMarker.remove();
//            }

            // myLocationMarker = mMap.addMarker(new MarkerOptions().position(myLocation).title("That's me ;)"));
            // mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

            if (marker == null)
                marker = mMap.addMarker(new MarkerOptions().position(myLocation).title("That's me ;)"));

            //Animate marker
            LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            MarkerAnimation.animateMarkerToGB(marker, myLocation, latLngInterpolator);


            if (!animated) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(myLocation).zoom(12).tilt(70).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
                animated = true;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            Log.e("Milerocker", "!EventBus.getDefault().isRegistered");
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
//        dismissProgressBar();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        dismissProgressBar();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
