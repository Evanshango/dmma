package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.Selection;

import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ItemHolder> {

    private List<Selection> mSelections;
    private SelectedItem mSelectedItem;

    public SelectionAdapter(List<Selection> selections, SelectedItem selectedItem) {
        mSelections = selections;
        mSelectedItem = selectedItem;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.selection_item, parent, false);
        return new ItemHolder(view, mSelectedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(mSelections.get(position));
    }

    @Override
    public int getItemCount() {
        return mSelections.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView selectionName;
        SelectedItem mSelectedItem;

        ItemHolder(@NonNull View itemView, SelectedItem selectedItem) {
            super(itemView);
            mSelectedItem = selectedItem;
            selectionName = itemView.findViewById(R.id.selectionName);
            itemView.setOnClickListener(this);
        }

        void bind(Selection selection) {
            selectionName.setText(selection.getSelection());
        }

        @Override
        public void onClick(View v) {
            mSelectedItem.itemSelected(mSelections.get(getAdapterPosition()));
        }
    }

    public interface SelectedItem{

        void itemSelected(Selection selection);
    }
}
