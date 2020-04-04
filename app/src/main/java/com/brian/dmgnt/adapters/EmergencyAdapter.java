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
        return mEmergencyContacts != null ? mEmergencyContacts.size() : 0;
    }

    class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ContactClick mContactClick;
        CircleImageView contactImage;
        TextView contactName, displayInitial, optionInfo, optionCall;
        RelativeLayout moreOptions, infoLayout;

        ContactHolder(@NonNull View itemView, ContactClick contactClick) {
            super(itemView);
            mContactClick = contactClick;
            contactImage = itemView.findViewById(R.id.contactImage);
            contactName = itemView.findViewById(R.id.contactName);
            moreOptions = itemView.findViewById(R.id.moreOptions);
            optionInfo = itemView.findViewById(R.id.optionInfo);
            optionCall = itemView.findViewById(R.id.optionCall);
            infoLayout = itemView.findViewById(R.id.infoLayout);
            displayInitial = itemView.findViewById(R.id.initial);

            infoLayout.setOnClickListener(this);
            optionInfo.setOnClickListener(this);
            optionCall.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mContactClick.contactClicked(
                    mEmergencyContacts.get(getAdapterPosition()), v, moreOptions);
        }

        void bind(EmergencyContact emergencyContact) {
            String name = emergencyContact.getContactName();
            String imageUrl = emergencyContact.getImageUrl();
            contactName.setText(name);
            Glide.with(itemView.getContext()).load(imageUrl).into(contactImage);

        }
    }

    public interface ContactClick {

        void contactClicked(
                EmergencyContact emergencyContact, View view, RelativeLayout moreOptions);
    }
}
