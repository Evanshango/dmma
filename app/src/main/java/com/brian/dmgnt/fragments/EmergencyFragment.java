package com.brian.dmgnt.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.adapters.EmergencyAdapter;
import com.brian.dmgnt.models.EmergencyContact;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.brian.dmgnt.helpers.Constants.CONTACTS;

public class EmergencyFragment extends Fragment implements EmergencyAdapter.ContactClick {

    private RecyclerView emergencyContactRv;
    private EmergencyAdapter mEmergencyAdapter;
    private List<EmergencyContact> mEmergencyContacts = new ArrayList<>();
    private CollectionReference contactsRef;
    private ProgressBar loadingProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        contactsRef = database.collection(CONTACTS);

        initViews(view);
        fetchContacts();
        return view;
    }

    private void fetchContacts() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        emergencyContactRv.setHasFixedSize(true);
        emergencyContactRv.setLayoutManager(manager);

        loadingProgress.setVisibility(View.VISIBLE);
        contactsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            mEmergencyContacts.addAll(queryDocumentSnapshots.toObjects(EmergencyContact.class));
            loadingProgress.setVisibility(View.GONE);
            mEmergencyAdapter = new EmergencyAdapter(mEmergencyContacts, this);
            emergencyContactRv.setAdapter(mEmergencyAdapter);
            mEmergencyAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            loadingProgress.setVisibility(View.GONE);
            Toast.makeText(getContext(), R.string.error_fetch, Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews(View view) {
        emergencyContactRv = view.findViewById(R.id.emergencyContact);
        loadingProgress = view.findViewById(R.id.loadingProgress);
    }

    @Override
    public void contactClicked(EmergencyContact contact, View view, RelativeLayout moreOptions) {
        switch (view.getId()) {
            case R.id.optionInfo:
                moreOptions.setVisibility(View.GONE);
                displayInfo(contact);
                break;
            case R.id.optionCall:
                moreOptions.setVisibility(View.GONE);
                makeCall(contact);
                break;
            case R.id.infoLayout:
                expandLayout(moreOptions);
                break;
        }
    }

    private void expandLayout(RelativeLayout moreOptions) {
        if (moreOptions.getVisibility() == View.GONE) {
            moreOptions.setVisibility(View.VISIBLE);
        } else {
            moreOptions.setVisibility(View.GONE);
        }
    }

    private void makeCall(EmergencyContact contact) {
        if (!contact.getContactNo().equals("")) {
            String phoneNo = "+" + contact.getContactNo();
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Your are about to make a phone call to: "
                    + phoneNo).setPositiveButton("Proceed", ((dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNo, null));
                startActivity(intent);
                dialog.dismiss();
            })).setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
            builder.create().show();
        } else {
            Toast.makeText(getContext(), "No Contact Number available", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayInfo(EmergencyContact contact) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert_info);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        initDialogViews(contact, dialog);
        dialog.show();
    }

    private void initDialogViews(EmergencyContact contact, Dialog dialog) {
        TextView contactName = dialog.findViewById(R.id.contact_name);
        TextView contactInfo = dialog.findViewById(R.id.contact_info);
        TextView closeDialog = dialog.findViewById(R.id.txtClose);
        TextView contactEmail = dialog.findViewById(R.id.contactEmail);

        contactName.setText(contact.getContactName());
        contactInfo.setText(contact.getContactInfo());
        contactEmail.setText(contact.getContactEmail());
        closeDialog.setOnClickListener(v -> dialog.dismiss());
    }
}
