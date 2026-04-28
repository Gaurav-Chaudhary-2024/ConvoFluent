package com.example.convofluent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LeaderboardActivity extends AppCompatActivity {

    private ImageButton btnBack, btnInfo, btnSettings;
    private LinearLayout tabGlobal, tabFriends;
    private TextView tvTabGlobal, tvTabFriends;
    private LinearLayout btnMyStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack        = findViewById(R.id.btnBack);
        btnInfo        = findViewById(R.id.btnInfo);
        btnSettings    = findViewById(R.id.btnSettings);
        tabGlobal      = findViewById(R.id.tabGlobal);
        tabFriends     = findViewById(R.id.tabFriends);
        tvTabGlobal    = findViewById(R.id.tvTabGlobal);
        tvTabFriends   = findViewById(R.id.tvTabFriends);
        btnMyStatistics = findViewById(R.id.btnMyStatistics);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnInfo.setOnClickListener(v ->
                Toast.makeText(this, "Leaderboard resets every week", Toast.LENGTH_SHORT).show());

        btnSettings.setOnClickListener(v ->
                Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show());

        tabGlobal.setOnClickListener(v -> {
            tvTabGlobal.setTextColor(0xFF6B3EFF);
            tvTabFriends.setTextColor(0xFF888888);
        });

        tabFriends.setOnClickListener(v -> {
            tvTabFriends.setTextColor(0xFF6B3EFF);
            tvTabGlobal.setTextColor(0xFF888888);
            Toast.makeText(this, "Friends leaderboard coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnMyStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}