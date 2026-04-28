package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout tabHome, tabLessons, tabAlphabet, tabProfile;
    private ImageButton btnNotification;
    private ImageButton btnPlay1, btnPlay2, btnPlay3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupClickListeners();

        // Prevent going back to onboarding once on home screen using modern API
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Stay on home screen or minimize app
                moveTaskToBack(true);
            }
        });
    }

    private void initViews() {
        btnNotification = findViewById(R.id.btnNotification);
        tabHome = findViewById(R.id.tabHome);
        tabLessons = findViewById(R.id.tabLessons);
        tabAlphabet = findViewById(R.id.tabAlphabet);
        tabProfile = findViewById(R.id.tabProfile);
        btnPlay1 = findViewById(R.id.btnPlay1);
        btnPlay2 = findViewById(R.id.btnPlay2);
        btnPlay3 = findViewById(R.id.btnPlay3);
    }

    private void setupClickListeners() {
        btnNotification.setOnClickListener(v ->
                Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show());

        tabHome.setOnClickListener(v ->
                Toast.makeText(this, "You are on Home", Toast.LENGTH_SHORT).show());

        tabLessons.setOnClickListener(v ->
                Toast.makeText(this, "Lessons coming soon!", Toast.LENGTH_SHORT).show());

        tabAlphabet.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlphabetActivity.class);
            startActivity(intent);
        });

        tabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        btnPlay1.setOnClickListener(v -> {
            Intent intent = new Intent(this, DailyExpressionsCommuting.class);
            startActivity(intent);
        });

        btnPlay2.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderingAtIzakaya.class);
            startActivity(intent);
        });

        btnPlay3.setOnClickListener(v -> {
            Intent intent = new Intent(this, AnimeSlangCasualTalk.class);
            startActivity(intent);
        });
    }
}
