package com.agencyemr.milerocker.gpstracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.agencyemr.milerocker.gpstracker.Utils.AppUtils;
import com.agencyemr.milerocker.gpstracker.Utils.Const;
import com.agencyemr.milerocker.gpstracker.Utils.DateTimeHelper;
import com.agencyemr.milerocker.gpstracker.Utils.LocationVo;
import com.agencyemr.milerocker.gpstracker.models.datamodels.DaoSession;
import com.agencyemr.milerocker.gpstracker.models.datamodels.LocationTracker;
import com.agencyemr.milerocker.gpstracker.models.datamodels.LocationTrackerDao;
import com.agencyemr.milerocker.gpstracker.toolbox.MySingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Grishma on 16/5/16.
 */
public class LocationUpdateService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final String TAG = "LocationUpdateService";
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    public static Boolean mRequestingLocationUpdates;
    public static boolean isEnded = false;
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;
    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    private ArrayList<LocationVo> mLocationData;
    private String imsiNumber;
    private String imeiNumber;

    @Override
    public void onCreate() {
        super.onCreate();
        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.

        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        imsiNumber = tMgr.getSubscriberId();
        imeiNumber = tMgr.getDeviceId();
        if (imsiNumber == null) {
            imsiNumber = new StringBuilder(imeiNumber).reverse().toString();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        Log.e(TAG,"onTaskRemoved");
//        // TODO Auto-generated method stub
//        Intent restartService = new Intent(getApplicationContext(),
//                this.getClass());
//        restartService.setPackage(getPackageName());
//        PendingIntent restartServicePI = PendingIntent.getService(
//                getApplicationContext(), 1, restartService,
//                PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
//
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        Log.d("LOC", "Service init...");
        isEnded = false;
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        if (mGoogleApiClient == null)
            buildGoogleApiClient();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        } else {
            mGoogleApiClient.connect();
        }
        return START_STICKY;
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended==");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();

        saveLocationData(location);

//        Toast.makeText(this, "Loc changed",
//                Toast.LENGTH_SHORT).show();
    }

    private void saveLocationData(Location location) {
//        Toast.makeText(this, "saveLocationData",
//                Toast.LENGTH_SHORT).show();
        LocationTracker locationTracker = new LocationTracker();
        locationTracker.setImei(imeiNumber);
        locationTracker.setLatitude(location.getLatitude());
        locationTracker.setLongitude(location.getLongitude());
        locationTracker.setStartLocationName(Const.getCompleteAddressString(this, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        locationTracker.setDeviation(location.getBearing());
        locationTracker.setId(null);

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        LocationTrackerDao locationTrackerDao = daoSession.getLocationTrackerDao();
        long id = locationTrackerDao.insert(locationTracker);
        locationTracker.setId(id);
        Log.e(TAG, "Loc inserted :" + id);

        EventBus.getDefault().post(location);

        if (AppUtils.checkInternetConnection(this)) {
            postLocationToServer(locationTracker);
        }


    }

    private void postLocationToServer(LocationTracker locationTracker) {
        //http://milerocker.agencyemr.com/api/triproutelogs/
        // ?Latitude=1
        // &Logitude=1
        // &Deviation=1
        // &ModifiedUser=1
        // &StartLocationName=we
        // &DeviceID=1
//        &LogDatetimeDevice=2016-07-08%2000:00:00.000

        String url = Const.BASE_URL;
        url += "?Latitude=" + locationTracker.getLatitude();
        url += "&Logitude=" + locationTracker.getLongitude();
        url += "&Deviation=" + locationTracker.getDeviation();
        url += "&ModifiedUser=" + locationTracker.getModifiedUserId();
        url += "&StartLocationName=" + URLEncoder.encode(locationTracker.getStartLocationName());
        url += "&DeviceID=" + URLEncoder.encode(locationTracker.getImei());
        url += "&LogDatetimeDevice=" + URLEncoder.encode(DateTimeHelper.getDate()+" "+DateTimeHelper.getTime());


        Log.e(TAG, "Url :" + url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, response + "");
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse :" + error.getMessage());
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient===");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    public void setLocationData() {

        mLocationData = new ArrayList<>();
        LocationVo mLocVo = new LocationVo();
        mLocVo.setLongitude(mCurrentLocation.getLongitude());
        mLocVo.setLatitude(mCurrentLocation.getLatitude());
        mLocVo.setLocAddress(Const.getCompleteAddressString(this, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        mLocationData.add(mLocVo);

    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        setLocationData();
//        Toast.makeText(this, "Latitude: =" + mCurrentLocation.getLatitude() + " Longitude:=" + mCurrentLocation
//                .getLongitude(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Latitude:==" + mCurrentLocation.getLatitude() + "\n Longitude:==" + mCurrentLocation.getLongitude
                ());

        //LocationDBHelper.getInstance(this).insertLocationDetails(mLocationData);
    }


    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mGoogleApiClient.connect();
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        //mLocationRequest.setSmallestDisplacement(5f);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        startLocationUpdates();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    ((App) getApplication()).getCurrentActivity(), 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        stopLocationUpdates();
                        break;
                }
            }
        });
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;

            // The final argument to {@code requestLocationUpdates()} is a LocationListener
            // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
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
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Log.i(TAG, " startLocationUpdates===");
            isEnded = true;
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            // It is a good practice to remove location requests when the activity is in a paused or
            // stopped state. Doing so helps battery performance and is especially
            // recommended in applications that request frequent location updates.

            Log.d(TAG, "stopLocationUpdates();==");
            // The final argument to {@code requestLocationUpdates()} is a LocationListener
            // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stopLocationUpdates();
    }


}
