package com.brian.dmgnt;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private NavController mNavController;
    private long backPressedTime;
    private Toast backToast;
    private TextView btnToNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        mNavController = Navigation.findNavController(this, R.id.homeHostFragment);

        NavigationUI.setupWithNavController(bottomNav, mNavController);

        initViews();

        btnToNotifications.setOnClickListener(v -> toNotifications());
    }

    private void initViews() {
        btnToNotifications = findViewById(R.id.btnToNotifications);
    }

    private void toNotifications() {
        Toast.makeText(this, "To notifications", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return mNavController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        int id = Objects.requireNonNull(mNavController.getCurrentDestination()).getId();
        if (id == R.id.homeFragment) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                finish();
                return;
            } else {
                backToast = Toast.makeText(this, "Press back again to exit",
                        Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
