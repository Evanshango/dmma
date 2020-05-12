package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.Country;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryHolder> {

    private ItemInteraction mItemInteraction;
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
        holder.bind(mCountries.get(position));
    }

    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    public void setCountries(List<Country> countries){
        mCountries = countries;
        notifyDataSetChanged();
    }

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
            mItemInteraction.countryClick(mCountries.get(getAdapterPosition()));
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
