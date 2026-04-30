package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout tabHome, tabLessons, tabAlphabet, tabProfile;
    private ImageButton  btnNotification;
    private ImageButton  btnPlay1, btnPlay2, btnPlay3;

    private TextView tvGreeting, tvStreak, tvXp, tvRank;
    private TextView tvProgress1, tvProgress2, tvProgress3;

    private DatabaseHelper db;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db     = DatabaseHelper.getInstance(this);
        userId = SessionManager.getUserId(this);

        initViews();
        loadUserData();
        setupClickListeners();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { moveTaskToBack(true); }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // refresh XP/streak whenever user returns
    }

    private void initViews() {
        btnNotification = findViewById(R.id.btnNotification);
        tabHome         = findViewById(R.id.tabHome);
        tabLessons      = findViewById(R.id.tabLessons);
        tabAlphabet     = findViewById(R.id.tabAlphabet);
        tabProfile      = findViewById(R.id.tabProfile);
        btnPlay1        = findViewById(R.id.btnPlay1);
        btnPlay2        = findViewById(R.id.btnPlay2);
        btnPlay3        = findViewById(R.id.btnPlay3);
        tvGreeting      = findViewById(R.id.tvGreeting);
        tvStreak        = findViewById(R.id.tvStreak);
        tvXp            = findViewById(R.id.tvXp);
        tvRank          = findViewById(R.id.tvRank);
        tvProgress1     = findViewById(R.id.tvProgress1);
        tvProgress2     = findViewById(R.id.tvProgress2);
        tvProgress3     = findViewById(R.id.tvProgress3);
    }

    private void loadUserData() {
        DatabaseHelper.User      user  = db.getUser(userId);
        DatabaseHelper.UserStats stats = db.getStats(userId);

        if (user != null && tvGreeting != null) {
            String firstName = user.fullName.contains(" ")
                    ? user.fullName.substring(0, user.fullName.indexOf(" "))
                    : user.fullName;
            tvGreeting.setText("Ohayou, " + firstName + "! 🇯🇵");
        }
        if (stats != null) {
            if (tvStreak != null) tvStreak.setText(stats.streak + " Day Streak 🔥");
            if (tvXp     != null) tvXp.setText(stats.xp + " XP 🎯");
            if (tvRank   != null) tvRank.setText(stats.rank > 0 ? "#" + stats.rank + " League 🏆" : "Unranked 🏆");
        }
        if (tvProgress1 != null)
            tvProgress1.setText(db.getLessonProgress(userId, "DailyExpressionsCommuting") + "%");
        if (tvProgress2 != null)
            tvProgress2.setText(db.getLessonProgress(userId, "OrderingAtIzakaya") + "%");
        if (tvProgress3 != null)
            tvProgress3.setText(db.getLessonProgress(userId, "AnimeSlangCasualTalk") + "%");
    }

    private void setupClickListeners() {
        btnNotification.setOnClickListener(v ->
                Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show());
        tabHome.setOnClickListener(v ->
                Toast.makeText(this, "You are on Home", Toast.LENGTH_SHORT).show());
        tabLessons.setOnClickListener(v  -> openLastLesson());
        tabAlphabet.setOnClickListener(v ->
                startActivity(new Intent(this, AlphabetActivity.class)));
        tabProfile.setOnClickListener(v  ->
                startActivity(new Intent(this, ProfileActivity.class)));
        btnPlay1.setOnClickListener(v ->
                startActivity(new Intent(this, DailyExpressionsCommuting.class)));
        btnPlay2.setOnClickListener(v ->
                startActivity(new Intent(this, OrderingAtIzakaya.class)));
        btnPlay3.setOnClickListener(v ->
                startActivity(new Intent(this, AnimeSlangCasualTalk.class)));
    }

    private void openLastLesson() {
        String last = db.getLastOpenedLesson(userId);
        if (last == null) { startActivity(new Intent(this, DailyExpressionsCommuting.class)); return; }
        switch (last) {
            case "OrderingAtIzakaya":
                startActivity(new Intent(this, OrderingAtIzakaya.class)); break;
            case "AnimeSlangCasualTalk":
                startActivity(new Intent(this, AnimeSlangCasualTalk.class)); break;
            default:
                startActivity(new Intent(this, DailyExpressionsCommuting.class)); break;
        }
    }
}