package com.example.native_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.native_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class chat extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // Edge-to-Edge UI adjustments
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
                startActivity(new Intent(chat.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                // Current Activity
                startActivity(new Intent(chat.this, profile.class));
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(chat.this, chat.class));
                return true;
            } else if (id == R.id.nav_score) {
                startActivity(new Intent(chat.this, score.class));
                return true;
            }
            return false;
        });

        // Initialize message input and send button
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        // Handle button click event to send message
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                // You can send the message to the server or display it in the UI
                Toast.makeText(chat.this, "Message sent: " + message, Toast.LENGTH_SHORT).show();
                etMessage.setText(""); // Clear the message field
            } else {
                // Display a toast if the message is empty
                Toast.makeText(chat.this, "Please type a message", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (this instanceof chat) {
            // Minimize the app instead of exiting completely
            moveTaskToBack(true);
        } else {
            // For other activities, go back to the previous activity
            super.onBackPressed();
        }
    }
}
