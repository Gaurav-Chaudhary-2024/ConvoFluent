package com.example.convofluent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;

public class StatisticsActivity extends AppCompatActivity {

    private ImageButton btnBack, btnSettings;
    private LinearLayout btnClaimLoot;
    private LinearLayout tabDashboard, tabLearn, tabProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack        = findViewById(R.id.btnBack);
        btnSettings    = findViewById(R.id.btnSettings);
        btnClaimLoot   = findViewById(R.id.btnClaimLoot);
        tabDashboard   = findViewById(R.id.tabDashboard);
        tabLearn       = findViewById(R.id.tabLearn);
        tabProfile     = findViewById(R.id.tabProfile);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSettings.setOnClickListener(v ->
                Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show());

        btnClaimLoot.setOnClickListener(v ->
                Toast.makeText(this, "Weekly loot claimed! 🎉", Toast.LENGTH_SHORT).show());

        tabDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        tabLearn.setOnClickListener(v ->
                Toast.makeText(this, "Learn coming soon!", Toast.LENGTH_SHORT).show());

        tabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}