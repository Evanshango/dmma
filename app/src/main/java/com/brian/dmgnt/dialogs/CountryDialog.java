package com.brian.dmgnt.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.adapters.CountryAdapter;
import com.brian.dmgnt.models.Country;
import com.brian.dmgnt.viewmodels.PandemicViewModel;

import java.util.ArrayList;
import java.util.List;

public class CountryDialog extends AppCompatDialogFragment implements CountryAdapter.ItemInteraction {

    private RecyclerView countryRecycler;
    private CountryAdapter mCountryAdapter;
    private SearchView mSearchView;
    private ProgressBar loaderProgress;
    private PandemicViewModel mPandemicViewModel;
    private List<Country> mCountries = new ArrayList<>();
    private ItemClick mItemClick;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mPandemicViewModel = new ViewModelProvider(this).get(PandemicViewModel.class);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.country_dialog, null);

        mSearchView = view.findViewById(R.id.searchView);
        countryRecycler = view.findViewById(R.id.countryRecycler);
        loaderProgress = view.findViewById(R.id.loaderProgress);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        getCountries();

        mCountryAdapter = new CountryAdapter(this);

        mSearchView.setOnClickListener(v -> {
            mSearchView.setQueryHint("Search Country By Name");
            mSearchView.setIconified(false);
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mCountryAdapter.getFilter().filter(query);
                if (mCountryAdapter.getItemCount() < 1) {
                    Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT).show();
                }
                closeKeyboard();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCountryAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return alertDialog;
    }

    private void getCountries() {
        loaderProgress.setVisibility(View.VISIBLE);
        mPandemicViewModel.getCountries();
        mPandemicViewModel.getCountryResponse().observe(this, countryResponse -> {
            if (countryResponse.getThrowable() == null) {
                loaderProgress.setVisibility(View.GONE);
                prepareCountries(countryResponse.getCountries());
            } else {
                loaderProgress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Check you connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepareCountries(List<Country> countries) {
        if (countries != null) {
            mCountries.clear();
            mCountries.addAll(countries);
            LinearLayoutManager manager = new LinearLayoutManager(requireContext());
            countryRecycler.setHasFixedSize(true);
            countryRecycler.setLayoutManager(manager);

            mCountryAdapter.setCountries(mCountries.subList(0, 6), mCountries);
            countryRecycler.setAdapter(mCountryAdapter);
        } else {
            Toast.makeText(requireContext(), "No Countries found", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mItemClick = (ItemClick) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement countryDialog");
        }
    }

    @Override
    public void countryClick(Country country) {
        mItemClick.itemInteraction(country);
    }

    public interface ItemClick {

        void itemInteraction(Country country);
    }
}
