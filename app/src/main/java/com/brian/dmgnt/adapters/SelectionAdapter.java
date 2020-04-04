package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.GeneralInfo;

import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ItemHolder> {

    private List<GeneralInfo> mGeneralInfos;
    private SelectedItem mSelectedItem;
    private double height;

    public SelectionAdapter(List<GeneralInfo> generalInfos, SelectedItem selectedItem) {
        mGeneralInfos = generalInfos;
        mSelectedItem = selectedItem;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.selection_item, parent, false);
        height = parent.getMeasuredHeight() * 0.5;
        return new ItemHolder(view, mSelectedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(mGeneralInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return mGeneralInfos.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView selectionName;
        SelectedItem mSelectedItem;
        CardView selectionCard;

        ItemHolder(@NonNull View itemView, SelectedItem selectedItem) {
            super(itemView);
            double finalHeight = height * 0.75;
            mSelectedItem = selectedItem;
            selectionName = itemView.findViewById(R.id.selectionName);
            selectionCard = itemView.findViewById(R.id.selectionCard);

            selectionCard.setMinimumHeight((int) finalHeight);
            selectionName.setMinHeight((int) finalHeight);

            itemView.setOnClickListener(this);
        }

        void bind(GeneralInfo generalInfo) {
            selectionName.setText(generalInfo.getInfoType());
        }

        @Override
        public void onClick(View v) {
            mSelectedItem.itemSelected(mGeneralInfos.get(getAdapterPosition()));
        }
    }

    public interface SelectedItem {

        void itemSelected(GeneralInfo generalInfo);
    }
}
