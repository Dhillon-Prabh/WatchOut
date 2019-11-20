package com.example.androidproj;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonPoint;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private DatabaseReference myLocationRef;
    private GeoFire geoFire;
    private List<LatLng> schoolZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //requests permission
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        //how to handle location requests
                        buildLocationRequest();
                        //builds markers upon receiving location requests
                        buildLocationCallBack();
                        //
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MapsActivity.this);

                        //Setting the school zone areas
                        //Handles location data
                        settingGeoFire();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "You must enable permission lol", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private void initArea() {
        schoolZone = new ArrayList<>();

        GeoJsonLayer layer = null;

        try {
            layer = new GeoJsonLayer(mMap, R.raw.schools,
                    getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        layer.addLayerToMap();

        for (GeoJsonFeature feature : layer.getFeatures()){
            GeoJsonPoint gp = ((GeoJsonPoint) feature.getGeometry());
            schoolZone.add(gp.getCoordinates());
        }
    }

    private void settingGeoFire() {

        myLocationRef = FirebaseDatabase.getInstance().getReference();
        geoFire = new GeoFire(myLocationRef);

    }


    private void buildLocationCallBack() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(final LocationResult locationResult){
                if(mMap != null){

                    geoFire.setLocation("You", new GeoLocation(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            //remove existing marker
                            if(currentUser != null) currentUser.remove();
                            //add marker to current location
                            currentUser = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(locationResult.getLastLocation().getLatitude(),
                                            locationResult.getLastLocation().getLongitude()))
                                    .title("You"));

                            //move camera after adding marker
                            mMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(currentUser.getPosition(), 12.0f));
                        }
                    });
                }
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        //Accuracy level - most accurate level possible
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Checks every 5 seconds
        locationRequest.setInterval(5000);
        //Could be faster than set interval
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
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

        initArea();

        mMap.getUiSettings().setZoomControlsEnabled(true);


        if(fusedLocationProviderClient != null){
            //Checks if app allows precise location checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }
            //Gets periodic update on location
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }


        //Radius for school zones
        for(LatLng latLng : schoolZone){
            mMap.addCircle(new CircleOptions().center(latLng)
                    .radius(160)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF)
                    .strokeWidth(5.0f)
            );

            //Queries when user in school zones - and triggers notification
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.5); //500m
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                private void sendNotification(String title, String content){
                    String NOTIFICATION_CHANNEL_ID = "edmt_multiple_location";
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                    //checks compatibility of build version
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification",
                                NotificationManager.IMPORTANCE_DEFAULT);

                        notificationChannel.setDescription("Channel description");
                        notificationChannel.enableLights(true);
                        notificationChannel.setLightColor(Color.RED);
                        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                        notificationChannel.enableVibration(true);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
                    builder.setContentTitle(title)
                            .setContentText(content)
                            .setAutoCancel(false)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

                    Notification notification = builder.build();
                    notificationManager.notify(new Random().nextInt(), notification);
                }

                //when user enters radius
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    sendNotification("EDMTDEV", String.format("%s entered a school zone", key));
                }

                //when user location updates and still in radius
                @Override
                public void onKeyExited(String key) {
                    sendNotification("EDMTDEV", String.format("%s left the school zone", key));
                }

                //when user leaves radius
                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    sendNotification("EDMTDEV", String.format("Please drive safely in school zone", key));
                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    Toast.makeText(getApplicationContext(), " "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }
}