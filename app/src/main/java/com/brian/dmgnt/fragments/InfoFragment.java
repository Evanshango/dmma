package com.brian.dmgnt.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.brian.dmgnt.InfoItemActivity;
import com.brian.dmgnt.PandemicActivity;
import com.brian.dmgnt.R;
import com.brian.dmgnt.adapters.SelectionAdapter;
import com.brian.dmgnt.models.GeneralInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.brian.dmgnt.helpers.Constants.GENERAL_INFO;
import static com.brian.dmgnt.helpers.Constants.PANDEMICS;

public class InfoFragment extends Fragment implements SelectionAdapter.ItemInteraction {

    private RecyclerView infoRecycler;
    private ProgressBar infoLoader;
    private CollectionReference generalInfoRef;
    private SelectionAdapter mSelectionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        generalInfoRef = database.collection(GENERAL_INFO);

        initViews(view);

        loadGeneralInfos();

        mSelectionAdapter = new SelectionAdapter(this);

        return view;
    }

    private void loadGeneralInfos() {
        infoLoader.setVisibility(View.VISIBLE);
        generalInfoRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<GeneralInfo> generalInfoList = queryDocumentSnapshots.toObjects(GeneralInfo.class);
            prepareRecycler(generalInfoList);
        });
    }

    private void prepareRecycler(List<GeneralInfo> generalInfoList) {
        if (generalInfoList != null){
            List<GeneralInfo> infos = new ArrayList<>(generalInfoList);
            loadItems(infos);
        } else {
            infoLoader.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Unable to fetch items", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadItems(List<GeneralInfo> infos) {
        infoLoader.setVisibility(View.GONE);
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        infoRecycler.setHasFixedSize(true);
        infoRecycler.setLayoutManager(manager);

        mSelectionAdapter.setData(infos);
        infoRecycler.setAdapter(mSelectionAdapter);
    }

    private void initViews(View view) {
        infoRecycler = view.findViewById(R.id.infoRecycler);
        infoLoader = view.findViewById(R.id.infoLoader);
    }

    @Override
    public void itemSelected(GeneralInfo generalInfo) {
        if (generalInfo.getInfoType().equals(PANDEMICS)){
            startActivity(new Intent(requireContext(), PandemicActivity.class));
        } else {
            Intent intent = new Intent(requireContext(), InfoItemActivity.class);
            intent.putExtra("infoItem", generalInfo);
            startActivity(intent);
        }
    }
}
