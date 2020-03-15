package com.brian.dmgnt.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.brian.dmgnt.helpers.UserClient;
import com.brian.dmgnt.models.User;
import com.brian.dmgnt.models.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import static com.brian.dmgnt.helpers.Constants.FASTEST_INTERVAL;
import static com.brian.dmgnt.helpers.Constants.UPDATE_INTERVAL;
import static com.brian.dmgnt.helpers.Constants.USER_LOCATION;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private FusedLocationProviderClient mFusedLocationClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        return START_NOT_STICKY;
    }

    private void getLocation() {
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            User user = ((UserClient) (getApplicationContext())).getUser();
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            UserLocation userLocation = new UserLocation(geoPoint, null, user);
                            saveUserLocation(userLocation);
                        }
                    }
                },
                Looper.myLooper());
    }

    private void saveUserLocation(UserLocation userLocation) {
        try {
            DocumentReference locationRef = FirebaseFirestore.getInstance().collection(USER_LOCATION)
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            locationRef.set(userLocation).addOnCompleteListener(task -> {
               if (task.isSuccessful()){
                   Log.d(TAG, "onComplete: \ninserted user location into database." +
                           "\n latitude: " + userLocation.getGeoPoint().getLatitude() +
                           "\n longitude: " + userLocation.getGeoPoint().getLongitude());
               }
            });

        } catch (NullPointerException e){
            Log.e(TAG, "saveUserLocation: User instance is null, stopping location service");
            Log.e(TAG, "saveUserLocation: NullPointerException: " + e.getMessage() );
            stopSelf();
        }
    }
}
