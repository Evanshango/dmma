package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.MedicalService;

import java.util.List;

public class MedicalServiceAdapter extends RecyclerView.Adapter<MedicalServiceAdapter.ServiceHolder> {

    private List<MedicalService> mMedicalServices;
    private Service mService;

    public MedicalServiceAdapter(List<MedicalService> medicalServices, Service service) {
        mMedicalServices = medicalServices;
        mService = service;
    }

    @NonNull
    @Override
    public ServiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.medical_service_item, parent, false);
        return new ServiceHolder(view, mService);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHolder holder, int position) {
        holder.bind(mMedicalServices.get(position));
    }

    @Override
    public int getItemCount() {
        return mMedicalServices.size();
    }

    class ServiceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Service mService;
        TextView name, hotline;

        ServiceHolder(@NonNull View itemView, Service service) {
            super(itemView);
            mService = service;
            name = itemView.findViewById(R.id.serviceName);
            hotline = itemView.findViewById(R.id.serviceHotline);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mService.medicalServiceClicked(mMedicalServices.get(getAdapterPosition()));
        }

        void bind(MedicalService medicalService) {
            name.setText(medicalService.getName());
            hotline.setText(medicalService.getHotline());
        }
    }

    public interface Service{

        void medicalServiceClicked(MedicalService medicalService);
    }
}
