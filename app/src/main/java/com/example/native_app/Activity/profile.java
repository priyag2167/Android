package com.example.native_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.native_app.Adapter.UserAdapter;
import com.example.native_app.Model.User;
import com.example.native_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class profile extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        fetchUserData();

        // Set up the logout button
        findViewById(R.id.logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Log out the user
            Intent intent = new Intent(profile.this, login.class); // Redirect to LoginActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Close the current activity
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_certificate) {
                startActivity(new Intent(profile.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                // Current Activity
                startActivity(new Intent(profile.this, profile.class));

                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(profile.this, chat.class));
                return true;
            } else if (id == R.id.nav_score) {
                startActivity(new Intent(profile.this, score.class));
                return true;
            }
            return false;
        });
    }

    private void fetchUserData() {
        progressBar.setVisibility(View.VISIBLE); // Show progress bar
        recyclerView.setVisibility(View.INVISIBLE); // Hide RecyclerView until data is loaded

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE); // Hide progress bar
                recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView after data load
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("profile", "Failed to read data", error.toException());
                progressBar.setVisibility(View.GONE); // Hide progress bar in case of error
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (this instanceof profile) {
            // Minimize the app instead of exiting completely
            moveTaskToBack(true);
        } else {
            // For other activities, go back to the previous activity
            super.onBackPressed();
        }
    }

}
