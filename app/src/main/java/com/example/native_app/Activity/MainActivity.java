package com.example.native_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.native_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Apply Edge-to-Edge system UI adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnWinCertificate = findViewById(R.id.btn_win_certificate);
        btnWinCertificate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WinCertificateActivity.class);
            startActivity(intent);
        });


        // Initialize BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_certificate) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, profile.class));
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(MainActivity.this, chat.class));
                return true;
            } else if (id == R.id.nav_score) {
                startActivity(new Intent(MainActivity.this, score.class));
                return true;
            }
            return false;
        });


    }
    @Override
    public void onBackPressed() {
        if (this instanceof MainActivity) {
            // Minimize the app instead of exiting completely
            moveTaskToBack(true);
        } else {
            // For other activities, go back to the previous activity
            super.onBackPressed();
        }
    }

}
