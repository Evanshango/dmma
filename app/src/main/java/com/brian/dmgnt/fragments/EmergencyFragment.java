package com.brian.dmgnt.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.adapters.EmergencyAdapter;
import com.brian.dmgnt.models.EmergencyContact;

public class EmergencyFragment extends Fragment implements EmergencyAdapter.ContactClick {

    private RecyclerView emergencyContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        emergencyContact = view.findViewById(R.id.emergencyContact);
    }

    @Override
    public void contactClicked(EmergencyContact contact, View view, RelativeLayout moreOptions) {
        switch (view.getId()) {
            case R.id.optionInfo:
                displayInfo();
                break;
            case R.id.optionCall:
                makeCall();
                break;
            case R.id.infoLayout:
                expandLayout(moreOptions);
                break;
        }
    }

    private void expandLayout(RelativeLayout moreOptions) {
        Toast.makeText(getContext(), "expanding layout", Toast.LENGTH_SHORT).show();
    }

    private void makeCall() {
        Toast.makeText(getContext(), "making a call", Toast.LENGTH_SHORT).show();
    }

    private void displayInfo() {
        Toast.makeText(getContext(), "fetching info", Toast.LENGTH_SHORT).show();
    }
}
