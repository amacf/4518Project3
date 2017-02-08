package org.amcafee.project3;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Activity lastActivity = null;
    public GoogleApiClient mApiClient;
    private Marker posMarker;
    public static final String RECEIVE_ACT = "org.amcafee.project3";
    LocalBroadcastManager mbManager;
    private TextView mCurrentActivityMessage;
    private ImageView mImgView;
    private MediaPlayer mWalkingMusic = null;
    private BroadcastReceiver mbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(RECEIVE_ACT)) {
                String mCurrentActivity = intent.getStringExtra("activityType");
                Activity newActivity = new Activity();
                newActivity.mType = mCurrentActivity;
                newActivity.mDate = new Date();
                if(mCurrentActivity=="still"){
                    mImgView.setImageResource(R.drawable.still);
                    if(mWalkingMusic != null) {
                        mWalkingMusic.release();
                        mWalkingMusic = null;
                    }
                } else if(mCurrentActivity=="running"){
                    if(lastActivity.mType != "running" && lastActivity.mType != "walking") {
                        mWalkingMusic = MediaPlayer.create(getApplicationContext(), R.raw.beat_02);
                        mWalkingMusic.setLooping(true);
                        mWalkingMusic.start();
                    }
                    mImgView.setImageResource(R.drawable.running);
                } else if(mCurrentActivity=="walking"){
                    if(lastActivity.mType != "running" && lastActivity.mType != "walking") {
                        mWalkingMusic = MediaPlayer.create(getApplicationContext(), R.raw.beat_02);
                        mWalkingMusic.setLooping(true);
                        mWalkingMusic.start();
                    }
                    mImgView.setImageResource(R.drawable.walking);
                } else if(mCurrentActivity=="driving"){
                    mImgView.setImageResource(R.drawable.in_vehicle);
                    if(mWalkingMusic != null) {
                        mWalkingMusic.release();
                        mWalkingMusic = null;
                    }
                }
                mCurrentActivityMessage.setText("You are currently " + mCurrentActivity);
                if(lastActivity != null && lastActivity.mType == newActivity.mType)
                    return;
                ActivityLab activityLab = ActivityLab.get(getApplicationContext());
                activityLab.addActivity(newActivity);
                if(lastActivity != null){
                    String message = "You have been " + lastActivity.mType + " for " + (newActivity.mDate.getTime() - lastActivity.mDate.getTime())/1000 + " seconds";
                    Toast mToast = Toast.makeText(context, message , Toast.LENGTH_LONG );
                    mToast.show();
                }
                lastActivity = newActivity;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mCurrentActivityMessage = (TextView) findViewById(R.id.mesView);
        mImgView = (ImageView) findViewById(R.id.imgView);


        Activity newActivity = new Activity();
        newActivity.mType = "still";
        newActivity.mDate = new Date();

        if(lastActivity == null)
            lastActivity = newActivity;

        mCurrentActivityMessage.setText("You are currently " + lastActivity.mType);
        if(lastActivity.mType=="still"){
            mImgView.setImageResource(R.drawable.still);
        } else if(lastActivity.mType=="running"){
            mImgView.setImageResource(R.drawable.running);
        } else if(lastActivity.mType=="walking"){
            mImgView.setImageResource(R.drawable.walking);
        } else if(lastActivity.mType=="driving") {
            mImgView.setImageResource(R.drawable.in_vehicle);
        }


        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mbManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(RECEIVE_ACT);
        mbManager.registerReceiver(mbReceiver, mFilter);
    }

    @Override
    protected void  onDestroy() {
        super.onDestroy();
        mbManager.unregisterReceiver(mbReceiver);
        if(mWalkingMusic != null) {
            mWalkingMusic.release();
            mWalkingMusic = null;
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

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mApiClient);
        if(mLastLocation != null){
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,mMap.getMaxZoomLevel()-4));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                LocationRequest mLocationRequest = LocationRequest.create()
                        .setInterval(5000)
                        .setFastestInterval(3000)
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 500, pendingIntent );


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest mLocationRequest = LocationRequest.create()
                    .setInterval(5000)
                    .setFastestInterval(3000)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,mMap.getMaxZoomLevel()-4));
    }

}

