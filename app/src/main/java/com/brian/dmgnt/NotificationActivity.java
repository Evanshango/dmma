package com.brian.dmgnt;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.adapters.NotificationAdapter;
import com.brian.dmgnt.models.Incident;
import com.brian.dmgnt.models.Notification;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.brian.dmgnt.helpers.Constants.INCIDENTS;
import static com.brian.dmgnt.helpers.Constants.NOTIFICATIONS;

public class NotificationActivity extends AppCompatActivity implements
        NotificationAdapter.NotificationClick {

    private List<Notification> mNotificationList;
    private RecyclerView notificationsRecycler;
    private String userName;
    private CollectionReference incidentsCollection, notsCollection;
    private Toolbar notificationsToolbar;
    private LinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        incidentsCollection = database.collection(INCIDENTS);
        notsCollection = database.collection(NOTIFICATIONS);

        Intent intent = getIntent();
        mNotificationList = intent.getParcelableArrayListExtra("notifications");
        userName = intent.getStringExtra("username");

        initViews();

        notificationsToolbar.setTitle("Notifications");
        setSupportActionBar(notificationsToolbar);

        setNotifications();
    }

    private void setNotifications() {

        NotificationAdapter adapter = new NotificationAdapter(mNotificationList, userName, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        notificationsRecycler.setHasFixedSize(true);
        notificationsRecycler.setLayoutManager(manager);

        notificationsRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initViews() {
        notificationsRecycler = findViewById(R.id.notificationsRecycler);
        notificationsToolbar = findViewById(R.id.notificationsToolbar);
        loading = findViewById(R.id.loading);
    }

    @Override
    public void notificationClicked(Notification notification) {
        loading.setVisibility(View.VISIBLE);
        incidentsCollection.document(notification.getIncidentId()).get().addOnSuccessListener(
                documentSnapshot -> {
                    Incident incident = documentSnapshot.toObject(Incident.class);
                    openDialog(incident);
                }
        );
    }

    private void openDialog(Incident incident) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.notification_dialog);
        dialog.setCancelable(false);
        iniDialogViews(dialog, incident);
        loading.setVisibility(View.GONE);
        dialog.show();
    }

    private void iniDialogViews(Dialog dialog, Incident incident) {
        TextView category = dialog.findViewById(R.id.incidentCategory);
        TextView date = dialog.findViewById(R.id.incidentDate);
        TextView description = dialog.findViewById(R.id.incidentDescription);
        TextView ok = dialog.findViewById(R.id.txtOk);

        category.setText(incident.getCategory());
        date.setText(incident.getDate());
        description.setText(incident.getDescription());

        ok.setOnClickListener(v -> closeDialog(dialog, incident));
    }

    private void closeDialog(Dialog dialog, Incident incident) {
        Map<String, Object> map = new HashMap<>();
        map.put("read", true);
        notsCollection.document(incident.getUserId()).collection(NOTIFICATIONS)
                .document(incident.getIncidentId()).set(map, SetOptions.merge());
        dialog.dismiss();
    }
}
