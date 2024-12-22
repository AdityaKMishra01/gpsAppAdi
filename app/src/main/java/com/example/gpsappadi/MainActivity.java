package com.example.gpsappadi;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {
    private static final long minUpdateTime = 1000; // 1 second
    private static final long minUpdateDistance = 100; // 100 meters
    private LocationManager locationManager;
    private Button btnCurrentLocation;
    private TextView tvLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCurrentLocation = findViewById(R.id.btn_curloc);
        tvLocation = findViewById(R.id.tv_location); // Initialize TextView
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationPermissionsAndFetch();
            }  });    }
    private void checkLocationPermissionsAndFetch() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1001); // 1001 is the request code
            return;        }
        // Fetch current location
        fetchCurrentLocation();
    }
    @SuppressLint("MissingPermission")
    private void fetchCurrentLocation() {
        // Check if GPS is enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_LONG).show();
            // Redirect to location settings
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }
        // Get the last known location and display it
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            String message = String.format(
                    "Longitude: %1$s, Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            tvLocation.setText(message); // Update TextView
        } else {
            Toast.makeText(this, "Fetching location updates...", Toast.LENGTH_SHORT).show();        }
        // Register location updates
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minUpdateTime,
                minUpdateDistance,
                new MyLocationListener()
        );    }
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            String message = String.format(
                    "Longitude: %1$s, Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            tvLocation.setText(message); // Update TextView
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(MainActivity.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(MainActivity.this, "GPS Disabled", Toast.LENGTH_SHORT).show();
        }
    }   }
