package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnSettings;
    private Button btnChangeDailyGoal, btnAdjustDifficulty;
    private LinearLayout tabHome, tabLessons, tabAlphabet, tabProfile;
    private LinearLayout statRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupClickListeners();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initViews() {
        btnSettings        = findViewById(R.id.btnSettings);
        btnChangeDailyGoal = findViewById(R.id.btnChangeDailyGoal);
        btnAdjustDifficulty = findViewById(R.id.btnAdjustDifficulty);
        tabHome            = findViewById(R.id.tabHome);
        tabLessons         = findViewById(R.id.tabLessons);
        tabAlphabet        = findViewById(R.id.tabAlphabet);
        tabProfile         = findViewById(R.id.tabProfile);
        statRanking = findViewById(R.id.statRanking);
    }

    private void setupClickListeners() {

        btnSettings.setOnClickListener(v ->
                Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show());

        btnChangeDailyGoal.setOnClickListener(v ->
                Toast.makeText(this, "Change daily goal coming soon!", Toast.LENGTH_SHORT).show());

        btnAdjustDifficulty.setOnClickListener(v ->
                Toast.makeText(this, "Adjust difficulty coming soon!", Toast.LENGTH_SHORT).show());

        statRanking.setOnClickListener(v -> {
            Intent intent = new Intent(this, LeaderboardActivity.class);
            startActivity(intent);
        });

        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        tabLessons.setOnClickListener(v ->
                Toast.makeText(this, "Lessons coming soon!", Toast.LENGTH_SHORT).show());

        tabAlphabet.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlphabetActivity.class);
            startActivity(intent);
        });

        tabProfile.setOnClickListener(v ->
                Toast.makeText(this, "You are on Profile", Toast.LENGTH_SHORT).show());
    }
}
