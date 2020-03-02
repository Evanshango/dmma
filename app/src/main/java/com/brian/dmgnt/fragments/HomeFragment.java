package com.brian.dmgnt.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brian.dmgnt.R;
import com.brian.dmgnt.adapters.SelectionAdapter;
import com.brian.dmgnt.models.Selection;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SelectionAdapter.SelectedItem {

    private RecyclerView selectionRecycler;
    private List<Selection> mSelectionList = new ArrayList<>();
    private SelectionAdapter mSelectionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSelectionAdapter = new SelectionAdapter(mSelectionList, this);
        populateSelections();
    }

    private void populateSelections() {
        mSelectionList.add(new Selection("001", "Educational Information"));
        mSelectionList.add(new Selection("002", "Medical Service"));
        mSelectionList.add(new Selection("003", "Disasters"));
        mSelectionList.add(new Selection("004", "First Aid"));

        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        selectionRecycler.setHasFixedSize(true);
        selectionRecycler.setLayoutManager(manager);
        selectionRecycler.setAdapter(mSelectionAdapter);
        mSelectionAdapter.notifyDataSetChanged();
    }

    private void initViews(View view) {
        selectionRecycler = view.findViewById(R.id.selectionRecycler);
    }

    @Override
    public void itemSelected(Selection selection) {
        Toast.makeText(getContext(), selection.getSelection() + " clicked", Toast.LENGTH_SHORT).show();
    }
}
