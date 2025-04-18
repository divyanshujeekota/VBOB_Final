package com.example.vbob_original;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DriverPage extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    String bus_num;
    private TextView latTextView;
    private TextView lonTextView;
    private Button startButton;
    String driverID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_page);

        latTextView = findViewById(R.id.latitude);
        lonTextView = findViewById(R.id.longitude);
        startButton = findViewById(R.id.current_location);


        //Reading Bus number of respective driver using driverID
        driverID=FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        // driverID is in the form of xyz@driver.com
        // removing @driver.com
        int index=driverID.indexOf("@");
        driverID=driverID.substring(0,index);
        findBusNumber(driverID);




        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    }, 1001);
        }


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(1000)  // Update interval in milliseconds
                .setMinUpdateIntervalMillis(500)  // Fastest update interval
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build();






        addGeofence();




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void findBusNumber(String driverID) {
        Toast.makeText(this, driverID, Toast.LENGTH_SHORT).show();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Driver_Detail").child(driverID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bus_num=snapshot.getValue(String.class);
                Toast.makeText(DriverPage.this, bus_num , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void addGeofence()
    {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    //Custom made geofence based on VIT's main gate longitude and latitude and doing +-0.001 with the it to make a squared fence
                    if (latitude>=12.8394 && latitude<=12.8414 && longitude>=80.1517 && longitude<=80.1537)
                    {
                        Toast.makeText(DriverPage.this, "Reached", Toast.LENGTH_SHORT).show();
                        stopLocationUpdates();
                        storeCurrentDateAndTime();

                    }

                    latTextView.setText("Latitude: " + latitude);
                    lonTextView.setText("Longitude: " + longitude);
                }
            }
        };


    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void storeCurrentDateAndTime()
    {

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dateString = currentDateTime.format(dateFormatter);


        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = currentDateTime.format(timeFormatter);
        Toast.makeText(this, dateString+" "+timeString+" "+bus_num, Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference("Arrival_Detail").child(dateString).child(bus_num).setValue(timeString);

    }




}