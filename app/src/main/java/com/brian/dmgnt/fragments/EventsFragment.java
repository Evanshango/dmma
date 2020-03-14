package com.brian.dmgnt.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.brian.dmgnt.R;
import com.brian.dmgnt.helpers.ClusterRenderer;
import com.brian.dmgnt.models.ClusterMarker;
import com.brian.dmgnt.models.Incident;
import com.brian.dmgnt.models.UserLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import static com.brian.dmgnt.helpers.Constants.INCIDENTS;
import static com.brian.dmgnt.helpers.Constants.MAP_VIEW_BUNDLE_KEY;
import static com.brian.dmgnt.helpers.Constants.USER_LOCATION;

public class EventsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "EventsFragment";
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private CollectionReference incidentsCollection, locationsCollection;
    private List<Incident> mIncidents = new ArrayList<>();
    private double bottomBoundary, topBoundary, leftBoundary, rightBoundary;
    private ClusterManager mClusterManager;
    private ClusterRenderer mClusterRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        locationsCollection = database.collection(USER_LOCATION);
        incidentsCollection = database.collection(INCIDENTS);

        initViews(view);
        initGoogleMap(savedInstanceState);

        if (user != null) {
            String userId = user.getUid();
            getUserLocation(userId);
        } else {
            Log.d(TAG, "onCreateView: User not logged in");
        }

        getIncidents();

        return view;
    }

    private void getUserLocation(String userId) {
        locationsCollection.document(userId).get().addOnSuccessListener(documentSnapshot -> {
            UserLocation userLocation = documentSnapshot.toObject(UserLocation.class);
            setMapBounds(userLocation);
        });
    }

    private void setMapBounds(UserLocation userLocation) {
        if (userLocation != null) {
            bottomBoundary = userLocation.getGeoPoint().getLatitude() - .1;
            leftBoundary = userLocation.getGeoPoint().getLongitude() - .1;
            topBoundary = userLocation.getGeoPoint().getLatitude() + .1;
            rightBoundary = userLocation.getGeoPoint().getLongitude() + .1;
        } else {
            Log.d(TAG, "setMapBounds: Unable to fetch user location");
        }
    }

    private void getIncidents() {
        incidentsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                mIncidents.addAll(queryDocumentSnapshots.toObjects(Incident.class));
                setMakers(mIncidents, mGoogleMap);
//                addMapMarkers(mIncidents);
            } else {
                Log.d(TAG, "getIncidents: No incidents");
            }
        }).addOnFailureListener(e -> Log.d(TAG, "getIncidents: An error occurred"));
    }

    private void addMapMarkers(List<Incident> incidents) {
        if (mGoogleMap != null) {
            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity()
                        .getApplicationContext(), mGoogleMap);
            }
            if (mClusterRenderer == null) {
                mClusterRenderer = new ClusterRenderer(getActivity(), mGoogleMap, mClusterManager);
                mClusterManager.setRenderer(mClusterRenderer);
            }
            for (Incident incident : incidents) {
                try {
                    ClusterMarker marker = new ClusterMarker(
                            new LatLng(incident.getGeoPoint().getLatitude(),
                                    incident.getGeoPoint().getLongitude()), incident.getCategory(),
                            incident.getDescription(), incident.getImageUrl(), incident
                    );
                    mClusterManager.addItem(marker);
                    mClusterMarkers.add(marker);
                } catch (NullPointerException e) {
                    Log.d(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
                }
            }
            mClusterManager.cluster();
            setCameraView();
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private void setCameraView() {
        LatLngBounds mapBounds = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBounds, 0));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStart();
    }

    private void initViews(View view) {
        mMapView = view.findViewById(R.id.map);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    private void setMakers(List<Incident> incidents, GoogleMap googleMap) {
        for (Incident incident : incidents){
            LatLng latLng = new LatLng(
                    incident.getGeoPoint().getLatitude(), incident.getGeoPoint().getLongitude()
            );
            googleMap.addMarker(new MarkerOptions().position(latLng).title(incident.getCategory())
            .snippet(incident.getDescription()));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18f));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));
        }
//        googleMap.animateCamera(CameraUpdateFactory.);
//        setCameraView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        googleMap.setOnMapLoadedCallback(this::getIncidents);
    }
}