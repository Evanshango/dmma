package com.brian.dmgnt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    private List<Notification> mNotifications;
    private String mUsername;
    private NotificationClick mNotificationClick;

    public NotificationAdapter(List<Notification> notifications, String username,
                               NotificationClick notificationClick) {
        mNotifications = notifications;
        mUsername = username;
        mNotificationClick = notificationClick;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notification_item, parent, false);
        return new NotificationHolder(view, mNotificationClick);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        holder.bind(mNotifications.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    class NotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        NotificationClick mNotificationClick;
        TextView notificationSender;

        NotificationHolder(@NonNull View itemView, NotificationClick notificationClick) {
            super(itemView);
            mNotificationClick = notificationClick;
            notificationSender = itemView.findViewById(R.id.notificationSender);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mNotificationClick.notificationClicked(mNotifications.get(getAdapterPosition()));
        }

        void bind(Notification notification) {
            if (notification.getSender().equals(mUsername)) {
                notificationSender.setText(R.string.incident_msg);
            } else {
                notificationSender.setText(String.format(
                        "%s reported an incident", notification.getSender()
                        )
                );
            }
        }
    }

    public interface NotificationClick {

        void notificationClicked(Notification notification);
    }
}
