package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton  btnSettings;
    private Button       btnChangeDailyGoal, btnAdjustDifficulty;
    private LinearLayout tabHome, tabLessons, tabAlphabet, tabProfile;
    private LinearLayout statRanking;

    // Dynamic TextViews
    private TextView tvName, tvSubtitle, tvLevel, tvXpProgress;
    private TextView tvStreak, tvRanking;
    private TextView tvDailyGoal, tvDifficulty;
    private TextView tvInterestsContainer;
    private ProgressBar pbXp;

    private DatabaseHelper db;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db     = DatabaseHelper.getInstance(this);
        userId = SessionManager.getUserId(this);

        initViews();
        loadProfileData();
        setupClickListeners();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void initViews() {
        btnSettings         = findViewById(R.id.btnSettings);
        btnChangeDailyGoal  = findViewById(R.id.btnChangeDailyGoal);
        btnAdjustDifficulty = findViewById(R.id.btnAdjustDifficulty);
        tabHome             = findViewById(R.id.tabHome);
        tabLessons          = findViewById(R.id.tabLessons);
        tabAlphabet         = findViewById(R.id.tabAlphabet);
        tabProfile          = findViewById(R.id.tabProfile);
        statRanking         = findViewById(R.id.statRanking);

        tvName              = findViewById(R.id.tvProfileName);
        tvSubtitle          = findViewById(R.id.tvProfileSubtitle);
        tvLevel             = findViewById(R.id.tvLevel);
        tvXpProgress        = findViewById(R.id.tvXpProgress);
        pbXp                = findViewById(R.id.pbXp);
        tvStreak            = findViewById(R.id.tvStreakValue);
        tvRanking           = findViewById(R.id.tvRankingValue);
        tvDailyGoal         = findViewById(R.id.tvDailyGoalValue);
        tvDifficulty        = findViewById(R.id.tvDifficultyValue);
    }

    private void loadProfileData() {
        DatabaseHelper.User        user  = db.getUser(userId);
        DatabaseHelper.UserStats   stats = db.getStats(userId);
        DatabaseHelper.UserPreferences prefs = db.getPreferences(userId);
        List<String> interests            = db.getInterests(userId);

        if (user != null) {
            if (tvName     != null) tvName.setText(user.fullName);
            if (tvSubtitle != null) {
                String lang = user.language != null ? user.language : "Japanese";
                tvSubtitle.setText("Learning " + lang);
            }
        }

        if (stats != null) {
            if (tvLevel != null) tvLevel.setText("LVL " + stats.level);

            // XP progress bar: 500 XP per level
            int xpIntoLevel   = stats.xp % 500;
            int xpForNextLevel = 500;
            if (tvXpProgress != null)
                tvXpProgress.setText(xpIntoLevel + " / " + xpForNextLevel + " XP");
            if (pbXp != null)
                pbXp.setProgress((int)((xpIntoLevel / 500f) * 100));

            if (tvStreak  != null) tvStreak.setText(stats.streak + " Days");
            if (tvRanking != null) tvRanking.setText(stats.rank > 0 ? "Top " + getRankPercentile(stats.rank) + "%" : "Unranked");
        }

        if (prefs != null) {
            if (tvDailyGoal  != null) tvDailyGoal.setText(prefs.dailyGoalMins + " min/day");
            if (tvDifficulty != null) tvDifficulty.setText(prefs.difficulty);
        }

        // Interests chips — update subtitle to show them
        if (tvSubtitle != null && user != null && !interests.isEmpty()) {
            String lang = user.language != null ? user.language : "Japanese";
            tvSubtitle.setText("Learning " + lang + " · " + interests.size() + " interests");
        }
    }

    /** Rough percentile based on rank number (out of 12 seeded users). */
    private int getRankPercentile(int rank) {
        if (rank <= 1)  return 1;
        if (rank <= 2)  return 5;
        if (rank <= 4)  return 10;
        if (rank <= 6)  return 25;
        if (rank <= 9)  return 50;
        return 75;
    }

    private void setupClickListeners() {
        btnSettings.setOnClickListener(v ->
                Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show());

        btnChangeDailyGoal.setOnClickListener(v -> showGoalPicker());

        btnAdjustDifficulty.setOnClickListener(v -> showDifficultyPicker());

        statRanking.setOnClickListener(v ->
                startActivity(new Intent(this, LeaderboardActivity.class)));

        tabHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        tabLessons.setOnClickListener(v -> {
            String last = db.getLastOpenedLesson(userId);
            Intent i = new Intent(this, last != null &&
                    last.equals("OrderingAtIzakaya") ? OrderingAtIzakaya.class :
                    last != null && last.equals("AnimeSlangCasualTalk") ?
                            AnimeSlangCasualTalk.class : DailyExpressionsCommuting.class);
            startActivity(i);
        });
        tabAlphabet.setOnClickListener(v ->
                startActivity(new Intent(this, AlphabetActivity.class)));
        tabProfile.setOnClickListener(v ->
                Toast.makeText(this, "You are on Profile", Toast.LENGTH_SHORT).show());
    }

    private void showGoalPicker() {
        int[] options = {5, 10, 15, 20, 30};
        String[] labels = {"5 min/day", "10 min/day", "15 min/day", "20 min/day", "30 min/day"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Daily Goal")
                .setItems(labels, (dialog, which) -> {
                    db.savePreferences(userId, options[which],
                            db.getPreferences(userId).difficulty);
                    if (tvDailyGoal != null) tvDailyGoal.setText(labels[which]);
                    Toast.makeText(this, "Goal updated!", Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void showDifficultyPicker() {
        String[] levels = {"Beginner", "Elementary A1", "Pre-Intermediate A2",
                "Intermediate B1", "Upper-Intermediate B2", "Advanced C1"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Difficulty Level")
                .setItems(levels, (dialog, which) -> {
                    db.savePreferences(userId,
                            db.getPreferences(userId).dailyGoalMins, levels[which]);
                    if (tvDifficulty != null) tvDifficulty.setText(levels[which]);
                    Toast.makeText(this, "Difficulty updated!", Toast.LENGTH_SHORT).show();
                }).show();
    }
}