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
    private ItemInteraction mItemInteraction;

    public SelectionAdapter(ItemInteraction itemInteraction) {
        mItemInteraction = itemInteraction;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.selection_item, parent, false);
        return new ItemHolder(view, mItemInteraction);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(mGeneralInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return mGeneralInfos.size();
    }

    public void setData(List<GeneralInfo> generalInfoList){
        mGeneralInfos = generalInfoList;
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView selectionName;
        ItemInteraction mItemInteraction;
        CardView selectionCard;

        ItemHolder(@NonNull View itemView, ItemInteraction itemInteraction) {
            super(itemView);
            mItemInteraction = itemInteraction;
            selectionName = itemView.findViewById(R.id.selectionName);
            selectionCard = itemView.findViewById(R.id.selectionCard);

            itemView.setOnClickListener(this);
        }

        void bind(GeneralInfo generalInfo) {
            selectionName.setText(generalInfo.getInfoType());
        }

        @Override
        public void onClick(View v) {
            mItemInteraction.itemSelected(mGeneralInfos.get(getAdapterPosition()));
        }
    }

    public interface ItemInteraction {

        void itemSelected(GeneralInfo generalInfo);
    }
}
