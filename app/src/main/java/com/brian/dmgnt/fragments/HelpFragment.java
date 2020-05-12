package com.brian.dmgnt.fragments;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.Disaster;
import com.brian.dmgnt.models.GeneralInfo;
import com.brian.dmgnt.models.Incident;
import com.brian.dmgnt.models.UserLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.brian.dmgnt.helpers.Constants.DISASTERS;
import static com.brian.dmgnt.helpers.Constants.GALLERY_REQUEST_CODE;
import static com.brian.dmgnt.helpers.Constants.GENERAL_INFO;
import static com.brian.dmgnt.helpers.Constants.INCIDENTS;
import static com.brian.dmgnt.helpers.Constants.INFO_TYPE;
import static com.brian.dmgnt.helpers.Constants.LONG_DATE;
import static com.brian.dmgnt.helpers.Constants.MAP_VIEW_BUNDLE_KEY;
import static com.brian.dmgnt.helpers.Constants.SHORT_DATE;
import static com.brian.dmgnt.helpers.Constants.UPLOADS;
import static com.brian.dmgnt.helpers.Constants.USER_LOCATION;

public class HelpFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HelpFragment";
    private ImageView mImageView;
    private Uri imageUri;
    private EditText helpRemarks;
    private AutoCompleteTextView category;
    private Button btnProceed;
    private CollectionReference incidentCollection, locationsCollection, generalInfoRef;
    private String incidentId, date, time, imageUrl, userId;
    private GeoPoint mGeoPoint;
    private NavController mNavController;
    private StorageReference mStorageReference;
    private GoogleMap mGoogleMap;
    private MapView userMapLocation;
    private List<String> disasterNames = new ArrayList<>();
    private ProgressBar makingRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference(UPLOADS);

        incidentCollection = database.collection(INCIDENTS);
        locationsCollection = database.collection(USER_LOCATION);
        generalInfoRef = database.collection(GENERAL_INFO);
        incidentId = incidentCollection.document().getId();

        time = new SimpleDateFormat(LONG_DATE, Locale.getDefault()).format(new Date());
        date = new SimpleDateFormat(SHORT_DATE, Locale.getDefault()).format(new Date());

        initViews(view);
        initGoogleMap(savedInstanceState);

        userId = user != null ? user.getUid() : "";
        getUserLocation(userId);
        fetchDisasters();
        return view;
    }

    private void fetchDisasters() {
        Query query = generalInfoRef.whereEqualTo(INFO_TYPE, "Disasters").limit(1);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                GeneralInfo generalInfo = snapshot.toObject(GeneralInfo.class);
                String infoId = generalInfo.getInfoId();
                loadDisasterItems(infoId);
            }
        });
    }

    private void loadDisasterItems(String infoId) {
        List<Disaster> disasters = new ArrayList<>();
        generalInfoRef.document(infoId).collection(DISASTERS).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Disaster disaster = snapshot.toObject(Disaster.class);
                        disasters.add(disaster);
                    }
                    fillCategorySpinner(disasters);
                }).addOnFailureListener(e -> Log.d(TAG, "loadDisasterItems: Error " + e));
    }

    private void fillCategorySpinner(List<Disaster> disasters) {
        if (disasters != null) {
            for (Disaster disaster : disasters) {
                String disasterName = disaster.getName();
                disasterNames.add(disasterName);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, disasterNames);
            category.setAdapter(adapter);
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        userMapLocation.onCreate(mapViewBundle);
        userMapLocation.getMapAsync(this);
    }

    private void initViews(View view) {
        userMapLocation = view.findViewById(R.id.userMap);
        mImageView = view.findViewById(R.id.openGallery);
        helpRemarks = view.findViewById(R.id.helpRemarks);
        category = view.findViewById(R.id.chooseCategory);
        btnProceed = view.findViewById(R.id.btnProceed);
        makingRequest = view.findViewById(R.id.makingRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        mImageView.setOnClickListener(v -> openGallery());
        btnProceed.setOnClickListener(v -> sendHelpRequest());
    }

    private void sendHelpRequest() {
        String selectedCategory = category.getText().toString().trim();
        String remarks = helpRemarks.getText().toString().trim();
        if (mGeoPoint != null) {
            if (!selectedCategory.isEmpty()) {
                if (disasterNames.contains(selectedCategory)) {
                    if (imageUri != null) {
                        if (!remarks.isEmpty()) {
                            btnProceed.setEnabled(false);
                            doMakeRequest(selectedCategory, remarks);
                        } else {
                            helpRemarks.setError("Description required");
                            helpRemarks.requestFocus();
                        }
                    } else {
                        Toast.makeText(getContext(), "Choose an image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Invalid disaster name", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please choose a category", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Check your location settings", Toast.LENGTH_SHORT).show();
        }
    }

    private void doMakeRequest(String selectedCategory, String remarks) {
        makingRequest.setVisibility(View.VISIBLE);
        Incident incident = new Incident(incidentId, selectedCategory, imageUrl, remarks, userId,
                date, time, mGeoPoint, false);
        incidentCollection.document(incidentId).set(incident).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                makingRequest.setVisibility(View.GONE);
                btnProceed.setEnabled(true);
                mNavController.navigate(R.id.action_helpFragment_to_eventsFragment);
            } else {
                makingRequest.setVisibility(View.GONE);
                btnProceed.setEnabled(true);
                Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            makingRequest.setVisibility(View.GONE);
            btnProceed.setEnabled(true);
            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show() ;
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select an Option"), GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            mImageView.setImageURI(imageUri);
            openConfirmDialog();
        } else {
            mImageView.setImageURI(null);
            Toast.makeText(getContext(), "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openConfirmDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert);
        iniDialogViews(dialog);
        dialog.show();
    }

    private void iniDialogViews(Dialog dialog) {
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView proceed = dialog.findViewById(R.id.proceed);
        ProgressBar uploadProgress = dialog.findViewById(R.id.uploadProgress);

        proceed.setOnClickListener(v -> uploadImage(dialog, uploadProgress, cancel));
        cancel.setOnClickListener(v -> {
            imageUri = null;
            mImageView.setImageURI(null);
            dialog.dismiss();
        });
    }

    private void uploadImage(Dialog dialog, ProgressBar progressBar, TextView cancel) {
        cancel.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        if (imageUri != null) {
            StorageReference fileRef = mStorageReference.child(incidentId)
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() /
                        taskSnapshot.getTotalByteCount());
                progressBar.incrementProgressBy((int) progress);
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "uploadImage: ImageUri is empty");
            dialog.dismiss();
        }
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void getUserLocation(String userId) {
        locationsCollection.document(userId).get().addOnSuccessListener(documentSnapshot -> {
            UserLocation userLocation = documentSnapshot.toObject(UserLocation.class);
            if (userLocation != null) {
                mGeoPoint = userLocation.getGeoPoint();
                setMyLocation(mGeoPoint, mGoogleMap);
            } else {
                Log.d(TAG, "onActivityCreated: Unable to get user location");
            }
        });
    }

    private void setMyLocation(GeoPoint geoPoint, GoogleMap googleMap) {
        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latLng).title("My Location")
                .snippet("I'm here"));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18f));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        userMapLocation.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        userMapLocation.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        userMapLocation.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        userMapLocation.onStop();
    }

    @Override
    public void onPause() {
        userMapLocation.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        userMapLocation.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        mGoogleMap = googleMap;
    }
}
