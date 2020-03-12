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
import com.brian.dmgnt.helpers.UserClient;
import com.brian.dmgnt.models.Incident;
import com.brian.dmgnt.models.UserLocation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.brian.dmgnt.helpers.Constants.GALLERY_REQUEST_CODE;
import static com.brian.dmgnt.helpers.Constants.INCIDENTS;
import static com.brian.dmgnt.helpers.Constants.LONG_DATE;
import static com.brian.dmgnt.helpers.Constants.UPLOADS;
import static com.brian.dmgnt.helpers.Constants.USER_LOCATION;

public class HelpFragment extends Fragment {

    private static final String TAG = "HelpFragment";
    private ImageView mImageView;
    private Uri imageUri;
    private EditText helpRemarks;
    private AutoCompleteTextView category;
    private Button btnProceed;
    private CollectionReference incidentCollection;
    private String incidentId, userId, timestamp;
    private GeoPoint mGeoPoint;
    private NavController mNavController;
    private String imageUrl;
    private StorageReference mStorageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mImageView = view.findViewById(R.id.openGallery);
        helpRemarks = view.findViewById(R.id.helpRemarks);
        category = view.findViewById(R.id.chooseCategory);
        btnProceed = view.findViewById(R.id.btnProceed);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        mImageView.setOnClickListener(v -> openGallery());

        btnProceed.setOnClickListener(v -> sendHelpRequest());
    }

    private void sendHelpRequest() {
        String selectedCategory = category.getText().toString();
        String remarks = helpRemarks.getText().toString().trim();
        if (mGeoPoint != null) {
            if (!selectedCategory.isEmpty()) {
                if (imageUri != null) {
                    if (!remarks.isEmpty()) {
                        doMakeRequest(selectedCategory, remarks);
                    } else {
                        helpRemarks.setError("Description required");
                        helpRemarks.requestFocus();
                    }
                } else {
                    Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please choose a category", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please check your location settings", Toast.LENGTH_SHORT).show();
        }
    }

    private void doMakeRequest(String selectedCategory, String remarks) {
        Incident incident = new Incident(
                incidentId, selectedCategory, imageUrl, remarks, userId, mGeoPoint, timestamp
        );
        incidentCollection.document(incidentId).set(incident).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mNavController.navigate(R.id.action_helpFragment_to_eventsFragment);
            } else {
                Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "An error occurred",
                Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference(UPLOADS);
        incidentCollection = database.collection(INCIDENTS);

        incidentId = incidentCollection.document().getId();
        userId = ((UserClient) Objects.requireNonNull(getActivity()).getApplicationContext())
                .getUser().getUserId();
        DocumentReference userLocationRef = database.collection(USER_LOCATION).document(userId);

        timestamp = new SimpleDateFormat(LONG_DATE, Locale.getDefault()).format(new Date());

        userLocationRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserLocation location = documentSnapshot.toObject(UserLocation.class);
                if (location != null) {
                    mGeoPoint = location.getGeoPoint();
                } else {
                    Log.d(TAG, "onActivityCreated: Unable to get user location");
                }
            } else {
                Toast.makeText(getContext(), "Unable to get user info", Toast.LENGTH_SHORT).show();
            }
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
        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert);
        iniDialogViews(dialog);
        dialog.show();
    }

    private void iniDialogViews(Dialog dialog) {
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView proceed = dialog.findViewById(R.id.proceed);
        ProgressBar uploadProgress = dialog.findViewById(R.id.uploadProgress);

        proceed.setOnClickListener(v -> uploadImage(dialog, uploadProgress));
        cancel.setOnClickListener(v -> {
            imageUri = null;
            mImageView.setImageURI(null);
            dialog.dismiss();
        });
    }

    private void uploadImage(Dialog dialog, ProgressBar progressBar) {
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
        ContentResolver contentResolver = Objects.requireNonNull(getContext()).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}
