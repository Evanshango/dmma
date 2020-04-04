package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.Educational;

import java.util.List;

public class EducationalAdapter extends RecyclerView.Adapter<EducationalAdapter.ItemHolder> {

    private List<Educational> mEducationalList;
    private EducationalItem mEducationalItem;

    public EducationalAdapter(List<Educational> educationalList, EducationalItem educationalItem) {
        mEducationalList = educationalList;
        mEducationalItem = educationalItem;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.educational_item, parent, false);
        return new ItemHolder(view, mEducationalItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(mEducationalList.get(position));
    }

    @Override
    public int getItemCount() {
        return mEducationalList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        EducationalItem mEducationalItem;
        TextView title;
        ItemHolder(@NonNull View itemView, EducationalItem educationalItem) {
            super(itemView);
            mEducationalItem = educationalItem;
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mEducationalItem.educationItemClicked(mEducationalList.get(getAdapterPosition()));
        }

        void bind(Educational educational) {
            title.setText(educational.getName());
        }
    }

    public interface EducationalItem{

        void educationItemClicked(Educational educational);
    }
}
