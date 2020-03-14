package com.brian.dmgnt;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.brian.dmgnt.helpers.UserClient;
import com.brian.dmgnt.models.User;
import com.brian.dmgnt.models.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import static com.brian.dmgnt.helpers.Constants.ERROR_DIALOG_REQUEST;
import static com.brian.dmgnt.helpers.Constants.PERMISSION_REQUEST_ACCESS_FINE_LOCATION;
import static com.brian.dmgnt.helpers.Constants.PERMISSION_REQUEST_ENABLE_GPS;
import static com.brian.dmgnt.helpers.Constants.USERS_REF;
import static com.brian.dmgnt.helpers.Constants.USER_LOCATION;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private NavController mNavController;
    private long backPressedTime;
    private Toast backToast;
    private TextView btnToNotifications;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;
    private FirebaseFirestore mDatabase;
    private String userId;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        mNavController = Navigation.findNavController(this, R.id.homeHostFragment);

        NavigationUI.setupWithNavController(bottomNav, mNavController);

        mUser = auth.getCurrentUser();

        initViews();

        btnToNotifications.setOnClickListener(v -> toNotifications());

    }

    private void getUserDetails() {
        if (mUserLocation == null) {
            mUserLocation = new UserLocation();
            DocumentReference userRef = mDatabase.collection(USERS_REF).document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    mUserLocation.setUser(user);
                    ((UserClient)getApplicationContext()).setUser(user);
                    getLastKnownLocation();
                } else {
                    getLastKnownLocation();
                }
            });
        }
    }

    private void saveUserLocation() {
        if (mUserLocation != null) {
            DocumentReference locationRef = mDatabase.collection(USER_LOCATION)
                    .document(userId);
            locationRef.set(mUserLocation).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
                            "\n latitude: " + mUserLocation.getGeoPoint().getLatitude() +
                            "\n longitude: " + mUserLocation.getGeoPoint().getLongitude());
                } else {
                    Toast.makeText(this, "An error occurred getting user location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d(TAG, "saveUserLocation: Unable to get user location");
        }
    }

    private void getLastKnownLocation() {
        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                if (location != null) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mUserLocation.setGeoPoint(geoPoint);
                    mUserLocation.setTimestamp(null);
                    saveUserLocation();
                }
            }
        });
    }

    private boolean checkMapServices() {
        if (isServiceOk()) {
            return isMapsEnabled();
        }
        return false;
    }

    private void buildAlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", ((dialog, which) -> {
                    Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSION_REQUEST_ENABLE_GPS);
                }));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getUserDetails();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServiceOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Unable to make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PERMISSION_REQUEST_ENABLE_GPS) {
            if (mLocationPermissionGranted) {
                getUserDetails();
            } else {
                getLocationPermission();
            }
        }
    }

    private boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertNoGps();
            return false;
        }
        return true;
    }

    private void initViews() {
        btnToNotifications = findViewById(R.id.btnToNotifications);
    }

    private void toNotifications() {
//        Toast.makeText(this, "To notifications", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return mNavController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        int id = Objects.requireNonNull(mNavController.getCurrentDestination()).getId();
        if (id == R.id.homeFragment) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                finish();
                return;
            } else {
                backToast = Toast.makeText(this, "Press back again to exit",
                        Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getUserDetails();
            } else {
                getLocationPermission();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser == null) {
            toAuthActivity();
        } else {
            userId = mUser.getUid();
        }
    }

    private void toAuthActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

//    private void getIncidents() {
//        List<Incident> incidents = new ArrayList<>();
//        incidentsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
//            if (queryDocumentSnapshots != null) {
//                incidents.addAll(queryDocumentSnapshots.toObjects(Incident.class));
//                emitIncidents(incidents);
//            } else {
//                Log.d(TAG, "getIncidents: No incidents");
//            }
//        }).addOnFailureListener(e -> Log.d(TAG, "getIncidents: An error occurred"));
//    }

//    private void emitIncidents(List<Incident> incidents) {
//        IncidentEvent event = new IncidentEvent();
//        event.setIncidents(incidents);
//        EventBus.getDefault().postSticky(event);
//    }
}
