package com.brian.dmgnt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.adapters.DisasterAdapter;
import com.brian.dmgnt.adapters.EducationalAdapter;
import com.brian.dmgnt.adapters.FirstAidAdapter;
import com.brian.dmgnt.adapters.MedicalServiceAdapter;
import com.brian.dmgnt.models.Disaster;
import com.brian.dmgnt.models.Educational;
import com.brian.dmgnt.models.FirstAid;
import com.brian.dmgnt.models.GeneralInfo;
import com.brian.dmgnt.models.MedicalService;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.brian.dmgnt.helpers.Constants.GENERAL_INFO;

public class InfoItemActivity extends AppCompatActivity implements DisasterAdapter.DisasterClick,
        EducationalAdapter.EducationalItem, MedicalServiceAdapter.Service,
        FirstAidAdapter.FirstAidItem {

    private String infoType, infoId;
    private Toolbar mToolbar;
    private RecyclerView itemInfoRecycler;
    private CollectionReference generalInfoRef;
    private ProgressBar itemsProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        generalInfoRef = database.collection(GENERAL_INFO);

        Intent intent = getIntent();
        GeneralInfo generalInfo = intent.getParcelableExtra("infoItem");
        if (generalInfo != null) {
            infoType = generalInfo.getInfoType();
            infoId = generalInfo.getInfoId();
        }
        initViews();
        mToolbar.setTitle(infoType);
        setSupportActionBar(mToolbar);

        getItemInfoList();
    }

    private void getItemInfoList() {
        switch (infoType) {
            case "Disasters":
                fetchItems("disasters");
                break;
            case "Educational":
                fetchItems("education");
                break;
            case "First Aid":
                fetchItems("firstAid");
                break;
            default:
                fetchItems("medicalServices");
                break;
        }
    }

    private void fetchItems(String pathName) {
        itemsProgress.setVisibility(View.VISIBLE);
        generalInfoRef.document(infoId).collection(pathName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        fetchSpecificSelectedItem(queryDocumentSnapshots, pathName);
                    }
                });
    }

    private void fetchSpecificSelectedItem(QuerySnapshot queryDocumentSnapshots, String pathName) {
        switch (pathName) {
            case "disasters":
                List<Disaster> disasters = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Disaster disaster = snapshot.toObject(Disaster.class);
                    disasters.add(disaster);
                }
                setDisasterItems(disasters);
                break;
            case "education":
                List<Educational> educationals = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Educational educational = snapshot.toObject(Educational.class);
                    educationals.add(educational);
                }
                setEducationalItems(educationals);
                break;
            case "firstAid":
                List<FirstAid> firstAids = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    FirstAid firstAid = snapshot.toObject(FirstAid.class);
                    firstAids.add(firstAid);
                }
                setFirstAidItems(firstAids);
                break;
            default:
                List<MedicalService> medicalServices = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    MedicalService medicalService = snapshot.toObject(MedicalService.class);
                    medicalServices.add(medicalService);
                }
                setMedicalServiceItems(medicalServices);
                break;
        }
    }

    private void setMedicalServiceItems(List<MedicalService> medicalServices) {
        MedicalServiceAdapter serviceAdapter = new MedicalServiceAdapter(medicalServices, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        itemInfoRecycler.setHasFixedSize(true);
        itemInfoRecycler.setLayoutManager(manager);
        itemInfoRecycler.setAdapter(serviceAdapter);
        serviceAdapter.notifyDataSetChanged();
        itemsProgress.setVisibility(View.GONE);
    }

    private void setDisasterItems(List<Disaster> disasters) {
        DisasterAdapter disasterAdapter = new DisasterAdapter(disasters, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        itemInfoRecycler.setHasFixedSize(true);
        itemInfoRecycler.setLayoutManager(manager);
        itemInfoRecycler.setAdapter(disasterAdapter);
        disasterAdapter.notifyDataSetChanged();
        itemsProgress.setVisibility(View.GONE);
    }

    private void setEducationalItems(List<Educational> educationals) {
        EducationalAdapter educationalAdapter = new EducationalAdapter(educationals, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        itemInfoRecycler.setHasFixedSize(true);
        itemInfoRecycler.setLayoutManager(manager);
        itemInfoRecycler.setAdapter(educationalAdapter);
        educationalAdapter.notifyDataSetChanged();
        itemsProgress.setVisibility(View.GONE);
    }

    private void setFirstAidItems(List<FirstAid> firstAids) {
        FirstAidAdapter aidAdapter = new FirstAidAdapter(firstAids, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        itemInfoRecycler.setHasFixedSize(true);
        itemInfoRecycler.setLayoutManager(manager);
        itemInfoRecycler.setAdapter(aidAdapter);
        aidAdapter.notifyDataSetChanged();
        itemsProgress.setVisibility(View.GONE);
    }

    private void initViews() {
        mToolbar = findViewById(R.id.itemToolbar);
        itemInfoRecycler = findViewById(R.id.itemInfoList);
        itemsProgress = findViewById(R.id.itemsProgress);
    }

    @Override
    public void disasterItemClicked(Disaster disaster) {
        String msg = " is part of " + disaster.getCategory() + " disasters";
        showInfoDialog(null, msg);
    }

    @Override
    public void educationItemClicked(Educational educational) {
        showInfoDialog(educational.getName(), educational.getExtraInfo());
    }

    @Override
    public void medicalServiceClicked(MedicalService medicalService) {
        if (!medicalService.getHotline().equals("")) {
            String phoneNo = medicalService.getHotline();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Call : " + phoneNo)
                    .setPositiveButton("Proceed", ((dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNo, null));
                        startActivity(intent);
                        dialog.dismiss();
                    })).setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
            builder.create().show();
        } else {
            Toast.makeText(this, "No Contact Number available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void aidItemClicked(FirstAid firstAid) {
        showInfoDialog(firstAid.getName(), firstAid.getAidInfo());
    }

    private void showInfoDialog(String title, String extraInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView resultMessage = new TextView(this);
        resultMessage.setTextSize(16);
        resultMessage.setText(extraInfo);
        resultMessage.setGravity(Gravity.CENTER);
        builder.setTitle(title)
                .setView(resultMessage)
                .setPositiveButton("Ok", ((dialog, which) -> dialog.dismiss()));
        builder.show();
    }
}
