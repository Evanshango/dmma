package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.Country;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryHolder> implements
        Filterable {

    private ItemInteraction mItemInteraction;
    private List<Country> mSublist;
    private List<Country> mCountries;

    public CountryAdapter(ItemInteraction itemInteraction) {
        mItemInteraction = itemInteraction;
    }

    @NonNull
    @Override
    public CountryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.country_item, parent, false);
        return new CountryHolder(view, mItemInteraction);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryHolder holder, int position) {
        holder.bind(mSublist.get(position));
    }

    @Override
    public int getItemCount() {
        return mSublist.size();
    }

    public void setCountries(List<Country> subList, List<Country> countries) {
        mSublist = subList;
        mCountries = new ArrayList<>(countries);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Country> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(mCountries);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Country country : mCountries){
                    if (country.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(country);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mSublist.clear();
            mSublist.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class CountryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemInteraction mItemInteraction;
        TextView countryName, countryInitial;

        CountryHolder(@NonNull View itemView, ItemInteraction itemInteraction) {
            super(itemView);
            mItemInteraction = itemInteraction;
            countryName = itemView.findViewById(R.id.countryName);
            countryInitial = itemView.findViewById(R.id.countryInitial);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemInteraction.countryClick(mSublist.get(getAdapterPosition()));
        }

        void bind(Country country) {
            countryName.setText(country.getName().toUpperCase());
            countryInitial.setText(country.getIso());
        }
    }

    public interface ItemInteraction{

        void countryClick(Country country);
    }
}
