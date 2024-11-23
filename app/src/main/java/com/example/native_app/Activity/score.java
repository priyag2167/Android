package com.example.native_app.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.native_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class score extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_certificate) {
                startActivity(new Intent(score.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                // Profile handled by MainActivity
                startActivity(new Intent(score.this, profile.class));
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(score.this, chat.class));
                return true;
            } else if (id == R.id.nav_score) {
                startActivity(new Intent(score.this, score.class));
                return true;
            }
            return false;
        });
    }
    @Override
    public void onBackPressed() {
        if (this instanceof score) {
            // Minimize the app instead of exiting completely
            moveTaskToBack(true);
        } else {
            // For other activities, go back to the previous activity
            super.onBackPressed();
        }
    }
}