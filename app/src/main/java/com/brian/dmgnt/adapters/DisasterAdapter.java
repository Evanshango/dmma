package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.Disaster;

import java.util.List;

public class DisasterAdapter extends RecyclerView.Adapter<DisasterAdapter.DisasterHolder> {

    private List<Disaster> mDisasters;
    private DisasterClick mDisasterClick;

    public DisasterAdapter(List<Disaster> disasters, DisasterClick disasterClick) {
        mDisasters = disasters;
        mDisasterClick = disasterClick;
    }

    @NonNull
    @Override
    public DisasterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.disaster_item, parent, false);
        return new DisasterHolder(view, mDisasterClick);
    }

    @Override
    public void onBindViewHolder(@NonNull DisasterHolder holder, int position) {
        holder.bind(mDisasters.get(position));
    }

    @Override
    public int getItemCount() {
        return mDisasters.size();
    }

    class DisasterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        DisasterClick mDisasterClick;
        TextView name;

        DisasterHolder(@NonNull View itemView, DisasterClick disasterClick) {
            super(itemView);
            mDisasterClick = disasterClick;
            name = itemView.findViewById(R.id.disasterName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mDisasterClick.disasterItemClicked(mDisasters.get(getAdapterPosition()));
        }

        void bind(Disaster disaster) {
            name.setText(disaster.getName());
        }
    }

    public interface DisasterClick {

        void disasterItemClicked(Disaster disaster);
    }
}
