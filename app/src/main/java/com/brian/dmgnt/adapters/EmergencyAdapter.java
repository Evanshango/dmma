package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.EmergencyContact;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.ContactHolder> {

    private List<EmergencyContact> mEmergencyContacts;
    private ContactClick mContactClick;

    public EmergencyAdapter(List<EmergencyContact> emergencyContacts, ContactClick contactClick) {
        mEmergencyContacts = emergencyContacts;
        mContactClick = contactClick;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.emergency_list_item, parent, false);
        return new ContactHolder(view, mContactClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        holder.bind(mEmergencyContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mEmergencyContacts.size();
    }

    class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ContactClick mContactClick;
        CircleImageView contactImage;
        TextView contactName, displayInitial;
        RelativeLayout moreOptions;

        ContactHolder(@NonNull View itemView, ContactClick contactClick) {
            super(itemView);
            mContactClick = contactClick;
            contactImage = itemView.findViewById(R.id.contactImage);
            contactName = itemView.findViewById(R.id.contactName);
            moreOptions = itemView.findViewById(R.id.moreOptions);
            displayInitial = itemView.findViewById(R.id.initial);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mContactClick.contactClicked(
                    mEmergencyContacts.get(getAdapterPosition()), v, moreOptions
            );
        }

        void bind(EmergencyContact emergencyContact) {
            String name = emergencyContact.getContactName();
            String imageUrl = emergencyContact.getImageUrl();
            contactName.setText(name);
            char initial = Character.toUpperCase(name.charAt(0));
            if ( imageUrl != null){
                displayInitial.setVisibility(View.GONE);
                contactImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(imageUrl).into(contactImage);
            } else {
                displayInitial.setVisibility(View.VISIBLE);
                contactImage.setVisibility(View.GONE);
                displayInitial.setText(initial);
            }
        }
    }

    public interface ContactClick {

        void contactClicked(EmergencyContact emergencyContact, View view, RelativeLayout moreOptions);
    }
}
