package com.brian.dmgnt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.brian.dmgnt.models.Notification;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private List<Notification> mNotificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Intent intent = getIntent();
        mNotificationList = intent.getParcelableArrayListExtra("notifications");
        if (mNotificationList != null){
            Toast.makeText(this, "size: " + mNotificationList.size(), Toast.LENGTH_SHORT).show();
        }
    }
}
