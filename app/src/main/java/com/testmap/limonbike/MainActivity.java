package com.testmap.limonbike;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, FetchAddressTask.OnTaskCompleted {

    private static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    GoogleMap map;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    ImageView imageView;


    TextView textView3;
    TextView textView4;
    TextView textView5;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private Button mLocationButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        if (1 == 1){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String name = user.getDisplayName();
            String[] output = name.split(":");
            if (output[0].equals("start")){
                imageView =(ImageView)findViewById(R.id.imageView);
                Drawable myDrawable = getResources().getDrawable(R.drawable.end_trip);
                imageView.setImageDrawable(myDrawable);

            }else{
                imageView =(ImageView)findViewById(R.id.imageView);
                Drawable myDrawable = getResources().getDrawable(R.drawable.scan_to_ride);
                imageView.setImageDrawable(myDrawable);
            }
        }

        textView3 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        mLocationButton = (Button) findViewById(R.id.button_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
                this);

        // Restore the state if the activity is recreated.
        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(
                    TRACKING_LOCATION_KEY);
        }

        // Set the listener for the location button.
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Toggle the tracking state.
             * @param v The track location button.
             */
            @Override
            public void onClick(View v) {
                if (!mTrackingLocation) {
                    startTrackingLocation();
                    textView5.setText(R.string.loading);/*
                    if ((textView3.getText().toString().equals(R.string.loading))){
                        textView5.setText("");
                    } else if(!(textView3.getText().toString().equals(R.string.loading))){
                        textView5.setText("SÜRÜŞÜN TADINI ÇIKARIN");
                    }*/
                } else {
                }
            }
        });

        // Initialize the location callbacks.
        mLocationCallback = new LocationCallback() {
            /**
             * This is the callback that is triggered when the
             * FusedLocationClient updates your location.
             * @param locationResult The result containing the device location.
             */
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // If tracking is turned on, reverse geocode into an address
                if (mTrackingLocation) {
                    new FetchAddressTask(MainActivity.this, MainActivity.this)
                            .execute(locationResult.getLastLocation());
                }
            }
        };


        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);

            // Set a loading text while you wait for the address to be
            // returned
            textView3.setText(getString(R.string.address_text,
                    getString(R.string.loading),
                    System.currentTimeMillis()));
            textView5.setText(getString(R.string.address_text,
                    getString(R.string.loading),
                    System.currentTimeMillis()));
            mLocationButton.setText(R.string.stop_tracking_location);

            //String[] a = textView3.getText().toString().split("Timestamp:");
            //Calendar now = Calendar.getInstance();
            //myRef.child("locations2").child("newgps").setValue(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));
        }
    }


    /**
     * Stops tracking the device. Removes the location
     * updates, stops the animation, and resets the UI.
     */
    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            mLocationButton.setText(R.string.start_tracking_location);
            textView3.setText(R.string.textview_hint);
        }
    }


    /**
     * Sets up the location request.
     *
     * @return The LocationRequest object containing the desired parameters.
     */
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    /**
     * Saves the last location on configuration change
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
        super.onSaveInstanceState(outState);
    }

    /**
     * Callback that is invoked when the user responds to the permissions
     * dialog.
     *
     * @param requestCode  Request code representing the permission request
     *                     issued by the app.
     * @param permissions  An array that contains the permissions that were
     *                     requested.
     * @param grantResults An array with the results of the request for each
     *                     permission requested.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:

                // If the permission is granted, get the location, otherwise,
                // show a Toast
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        if (mTrackingLocation) {
            // Update the UI
            textView3.setText(getString(R.string.address_text,
                    result, System.currentTimeMillis()));
            textView5.setText(getString(R.string.enjoy_the_ride));
        }
    }

    @Override
    protected void onPause() {
        if (mTrackingLocation) {
            stopTrackingLocation();
            mTrackingLocation = true;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mTrackingLocation) {
            startTrackingLocation();
        }
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;



        try {
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(

                            this, R.raw.dark_google_style));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

        map.setMaxZoomPreference(16);
        loginToFirebase();
        /*map = googleMap;

        LatLng Kadıkoy = new LatLng(40.964753, 29.076080);
        map.addMarker(new MarkerOptions().position(Kadıkoy).title("Kadıkoyy"));
        map.moveCamera(CameraUpdateFactory.newLatLng(Kadıkoy));
*/
        /*DatabaseReference newReference = firebaseDatabase.getReference("bicycles");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    HashMap<Double, Double> hashMap = (HashMap<Double, Double>) ds.getValue();

                    /*Double x = ;
                    Double y =*/
        //LatLng Kadıkoy = new LatLng(hashMap.get("x"), hashMap.get("y"));
        //map.addMarker(new MarkerOptions().position(Kadıkoy).title("Kadıkoyy"));
        //map.moveCamera(CameraUpdateFactory.newLatLng(Kadıkoy));

        //}

        //}

        //@Override
        //public void onCancelled(@NonNull DatabaseError databaseError) {

        //  }
        //});

    }

    private void loginToFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        String password = "123456";
        // Authenticate with Firebase and subscribe to updates
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    subscribeToUpdates();
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, map.addMarker(new MarkerOptions().title(key).position(location)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
    }


    /////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
    }

    public void refresh(View view){
        if (!(textView3.getText().toString().equals("notready"))){

        String[] output = textView3.getText().toString().split("Türkiye");
        String[] output2 = output[1].split("-");
        String[] output3 = output2[1].split("Timestamp");
        String[] output4 = output3[0].split("\\n");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        double result = Double.parseDouble(output2[0]);
        double result2 = Double.parseDouble(output4[0]);
        LatLng location = new LatLng(result2, result);
        map.addMarker(new MarkerOptions().position(location).title("siz"));


        CircleOptions circleOptionsLittle = new CircleOptions()

                .center(new LatLng(result2, result))
                .radius(30) // radius in meters
                .fillColor(0x8800CCFF) //this is a half transparent blue, change "88" for the transparency
                .strokeColor(Color.BLUE) //The stroke (border) is blue
                .strokeWidth(2); // The width is in pixel, so try it!


        CircleOptions circleOptions = new CircleOptions()

                .center(new LatLng(result2, result))
                .radius(300) // radius in meters
                .fillColor(Color.argb(97, 93, 185, 139)) //this is a half transparent blue, change "88" for the transparency
                .strokeColor(Color.argb(20, 93, 185, 139)) //The stroke (border) is blue
                .strokeWidth(2); // The width is in pixel, so try it!

        // Get back the mutable Circle
        map.addCircle(circleOptions);
        map.addCircle(circleOptionsLittle);
        CameraPosition cameraPos = new CameraPosition.Builder().tilt(60).target(location).zoom(15).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null);

        map.setMaxZoomPreference(16);
        }
        else {
            textView5.setText("Lütfen konumunuzun açık olduğundan emin olunuz.");
        }

    }

    public void startEndTripActivity(View view) {
        //Date currentTime = Calendar.getInstance().getTime();

        //myRef.child("918273645").child("enddate").setValue(currentTime);

        Intent intent = new Intent(getApplicationContext(),endTripActivity.class);
        startActivity(intent);
    }

    public void startQrScreenAct(View view){

        Intent intent = new Intent(getApplicationContext(),changeActivity.class);
        startActivity(intent);
        /*CircleOptions circleOptions = new CircleOptions()

                .center(new LatLng(result, result2))
                .radius(30) // radius in meters
                .fillColor(0x8800CCFF) //this is a half transparent blue, change "88" for the transparency
                .strokeColor(Color.BLUE) //The stroke (border) is blue
                .strokeWidth(2); // The width is in pixel, so try it!

        // Get back the mutable Circle
        map.addCircle(circleOptions);*/

        //LatLng location = new LatLng(result, result2);
        //LatLng location = new LatLng(result, result2);
        //map.addMarker(new MarkerOptions().position(location).title("anan"));
        //myRef.child("user").child("startdate").child("rnd").setValue(email);

        //Intent intent = new Intent(getApplicationContext(),changeActivity.class);
        //intent.putExtra("L1Value", output2[0]);
        //intent.putExtra("L2Value", output4[0]);
        //intent.putExtra("emailValue", email);
        //startActivity(intent);

        //myRef.child("locations").child("gps").child("longitude").setValue(output2[0]);
        //myRef.child("locations").child("gps").child("latitude").setValue(output4[0]);
    }
}
