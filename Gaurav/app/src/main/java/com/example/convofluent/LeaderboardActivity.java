package com.example.convofluent;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private ImageButton  btnBack, btnInfo, btnSettings;
    private LinearLayout tabGlobal, tabFriends;
    private TextView     tvTabGlobal, tvTabFriends;
    private LinearLayout btnMyStatistics;
    private LinearLayout leaderboardContainer;

    // Bottom sticky card
    private TextView tvMyRank, tvMyUsername, tvMyXp, tvMyXpToNext;

    private DatabaseHelper db;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        db     = DatabaseHelper.getInstance(this);
        userId = SessionManager.getUserId(this);
        initViews();
        loadLeaderboard();
        setupClickListeners();
    }

    private void initViews() {
        btnBack              = findViewById(R.id.btnBack);
        btnInfo              = findViewById(R.id.btnInfo);
        btnSettings          = findViewById(R.id.btnSettings);
        tabGlobal            = findViewById(R.id.tabGlobal);
        tabFriends           = findViewById(R.id.tabFriends);
        tvTabGlobal          = findViewById(R.id.tvTabGlobal);
        tvTabFriends         = findViewById(R.id.tvTabFriends);
        btnMyStatistics      = findViewById(R.id.btnMyStatistics);
        leaderboardContainer = findViewById(R.id.leaderboardContainer);
        tvMyRank             = findViewById(R.id.tvMyRank);
        tvMyUsername         = findViewById(R.id.tvMyUsername);
        tvMyXp               = findViewById(R.id.tvMyXp);
        tvMyXpToNext         = findViewById(R.id.tvMyXpToNext);
    }

    private void loadLeaderboard() {
        // Load top 10
        List<DatabaseHelper.LeaderboardEntry> entries = db.getTopLeaderboard(10);
        DatabaseHelper.LeaderboardEntry me = db.getMyLeaderboardEntry(userId);

        // Build rows dynamically
        if (leaderboardContainer != null) {
            leaderboardContainer.removeAllViews();
            for (DatabaseHelper.LeaderboardEntry e : entries) {
                leaderboardContainer.addView(buildRow(e, e.userId == userId));
            }
        }

        // Update sticky bottom card
        if (me != null) {
            if (tvMyRank     != null) tvMyRank.setText("#" + me.rank);
            if (tvMyUsername != null) tvMyUsername.setText(me.username);
            if (tvMyXp       != null) tvMyXp.setText(me.xp + " XP");

            // Find the entry just above current user
            int xpToNext = 0;
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).userId == userId && i > 0) {
                    xpToNext = entries.get(i - 1).xp - me.xp;
                    break;
                }
            }
            if (tvMyXpToNext != null) {
                tvMyXpToNext.setText(xpToNext > 0
                        ? xpToNext + " XP to reach Rank #" + (me.rank - 1)
                        : "You're at the top! 🏆");
            }
        }
    }

    private LinearLayout buildRow(DatabaseHelper.LeaderboardEntry e, boolean isMe) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 0, 0, dp(8));
        row.setLayoutParams(rowParams);
        row.setBackgroundResource(isMe
                ? R.drawable.leaderboard_you_row
                : R.drawable.leaderboard_row_bg);
        row.setPadding(dp(12), dp(12), dp(12), dp(12));

        // Rank number
        TextView tvRank = new TextView(this);
        LinearLayout.LayoutParams rankParams = new LinearLayout.LayoutParams(dp(36), dp(36));
        rankParams.setMargins(0, 0, dp(10), 0);
        tvRank.setLayoutParams(rankParams);
        tvRank.setText(String.valueOf(e.rank));
        tvRank.setTextSize(15f);
        tvRank.setTypeface(null, Typeface.BOLD);
        tvRank.setGravity(Gravity.CENTER);
        tvRank.setTextColor(e.rank == 1 ? 0xFFFFD700 :
                e.rank == 2 ? 0xFFC0C0C0 :
                        e.rank == 3 ? 0xFFCD7F32 : 0xFF1A1A2E);
        row.addView(tvRank);

        // Avatar circle
        TextView avatar = new TextView(this);
        LinearLayout.LayoutParams avParams = new LinearLayout.LayoutParams(dp(40), dp(40));
        avParams.setMargins(0, 0, dp(10), 0);
        avatar.setLayoutParams(avParams);
        avatar.setText(e.fullName.substring(0, 1).toUpperCase());
        avatar.setTextSize(16f);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);
        avatar.setTextColor(0xFFFFFFFF);
        avatar.setBackgroundResource(R.drawable.avatar_background);
        row.addView(avatar);

        // Name + country column
        LinearLayout nameCol = new LinearLayout(this);
        nameCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        nameCol.setLayoutParams(nameParams);

        TextView tvName = new TextView(this);
        tvName.setText(e.fullName);
        tvName.setTextSize(14f);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setTextColor(isMe ? 0xFF6B3EFF : 0xFF1A1A2E);
        nameCol.addView(tvName);

        TextView tvCountry = new TextView(this);
        tvCountry.setText(e.countryCode + "  LVL " + e.level);
        tvCountry.setTextSize(11f);
        tvCountry.setTextColor(0xFF888888);
        nameCol.addView(tvCountry);
        row.addView(nameCol);

        // XP
        TextView tvXp = new TextView(this);
        tvXp.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tvXp.setText(e.xp + " XP");
        tvXp.setTextSize(13f);
        tvXp.setTypeface(null, Typeface.BOLD);
        tvXp.setTextColor(isMe ? 0xFF6B3EFF : 0xFF1A1A2E);
        row.addView(tvXp);

        return row;
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
            loadLeaderboard();
        });
        tabFriends.setOnClickListener(v -> {
            tvTabFriends.setTextColor(0xFF6B3EFF);
            tvTabGlobal.setTextColor(0xFF888888);
            Toast.makeText(this, "Friends leaderboard coming soon!", Toast.LENGTH_SHORT).show();
        });
        btnMyStatistics.setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class)));
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}