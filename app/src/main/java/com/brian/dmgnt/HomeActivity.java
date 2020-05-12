package com.brian.dmgnt;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import com.brian.dmgnt.models.Notification;
import com.brian.dmgnt.models.User;
import com.brian.dmgnt.models.UserLocation;
import com.brian.dmgnt.services.LocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.brian.dmgnt.helpers.Constants.CREATED_AT;
import static com.brian.dmgnt.helpers.Constants.ERROR_DIALOG_REQUEST;
import static com.brian.dmgnt.helpers.Constants.NOTIFICATIONS;
import static com.brian.dmgnt.helpers.Constants.PERMISSION_REQUEST_ACCESS_FINE_LOCATION;
import static com.brian.dmgnt.helpers.Constants.PERMISSION_REQUEST_ENABLE_GPS;
import static com.brian.dmgnt.helpers.Constants.READ;
import static com.brian.dmgnt.helpers.Constants.RECEIVER;
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
    private CollectionReference notsCollection;
    private String userId, userName;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        notsCollection = mDatabase.collection(NOTIFICATIONS);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        mNavController = Navigation.findNavController(this, R.id.homeHostFragment);

        NavigationUI.setupWithNavController(bottomNav, mNavController);

        mUser = auth.getCurrentUser();
        userId = mUser != null ? mUser.getUid() : null;
        initViews();
        getNotifications();
    }

    private void getNotifications() {
        if (userId != null) {
            Query query = notsCollection.document(userId).collection(NOTIFICATIONS)
                    .whereEqualTo(RECEIVER, userId).whereEqualTo(READ, false)
                    .orderBy(CREATED_AT, Query.Direction.DESCENDING);
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<Notification> notifications = new ArrayList<>();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Notification notification = snapshot.toObject(Notification.class);
                        notifications.add(notification);
                    }
                    setNotificationCount(notifications);
                } else {
                    btnToNotifications.setText("0");
                }
            });
        }
    }

    private void setNotificationCount(List<Notification> notifications) {
        int totalNotifications = notifications.size();
        btnToNotifications.setText(String.valueOf(totalNotifications));
        btnToNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("username", userName);
            intent.putParcelableArrayListExtra("notifications", (ArrayList<Notification>) notifications);
            startActivity(intent);
        });
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationService.class);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                HomeActivity.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.brian.dmgnt.services.LocationService".equals(serviceInfo.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running");
        return false;
    }

    private void getUserDetails() {
        if (mUserLocation == null) {
            mUserLocation = new UserLocation();
            DocumentReference userRef = mDatabase.collection(USERS_REF).document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    mUserLocation.setUser(user);
                    assert user != null;
                    userName = user.getUsername();
                    ((UserClient) getApplicationContext()).setUser(user);
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
                    startLocationService();
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
        btnToNotifications = findViewById(R.id.txtNotifications);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return mNavController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        int id = Objects.requireNonNull(mNavController.getCurrentDestination()).getId();
        if (id == R.id.eventsFragment) {
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
        getNotifications();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser == null) {
            toAuthActivity();
        } else {
            String uid = mUser.getUid();
            listenForNotificationsChanges(uid);
        }
    }

    private void listenForNotificationsChanges(String uid) {
        notsCollection.document(uid).collection(NOTIFICATIONS)
                .addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
                   if (e != null){
                       Toast.makeText(this, "Error loading notifications", Toast.LENGTH_SHORT).show();
                       return;
                   }
                    List<Notification> notifications = new ArrayList<>();
                   if (queryDocumentSnapshots != null){
                       for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                           Notification notification = snapshot.toObject(Notification.class);
                           notifications.add(notification);
                       }
                       setNotificationCount(notifications);
                   }
                });
    }

    private void toAuthActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
