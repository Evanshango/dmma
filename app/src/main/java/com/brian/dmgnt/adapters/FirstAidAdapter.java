package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.FirstAid;

import java.util.List;

public class FirstAidAdapter extends RecyclerView.Adapter<FirstAidAdapter.FirstAidHolder> {

    private List<FirstAid> mAidList;
    private FirstAidItem mFirstAidItem;

    public FirstAidAdapter(List<FirstAid> firstAidList, FirstAidItem firstAidItem) {
        mAidList = firstAidList;
        mFirstAidItem = firstAidItem;
    }

    @NonNull
    @Override
    public FirstAidHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.first_aid_item, parent, false);
        return new FirstAidHolder(view, mFirstAidItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FirstAidHolder holder, int position) {
        holder.bind(mAidList.get(position));
    }

    @Override
    public int getItemCount() {
        return mAidList.size();
    }

    class FirstAidHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FirstAidItem mFirstAid;
        TextView name;

        FirstAidHolder(@NonNull View itemView, FirstAidItem firstAid) {
            super(itemView);
            mFirstAid = firstAid;
            name = itemView.findViewById(R.id.aidTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mFirstAid.aidItemClicked(mAidList.get(getAdapterPosition()));
        }

        void bind(FirstAid firstAid) {
            name.setText(firstAid.getName());
        }
    }

    public interface FirstAidItem{

        void aidItemClicked(FirstAid firstAid);
    }
}
