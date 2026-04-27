package com.example.convofluent;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvLeaderboard;
    private LeaderboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        rvLeaderboard = findViewById(R.id.rvLeaderboard);
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));

        List<LeaderboardItem> data = new ArrayList<>();
        // Rank, AvatarText, Name, Country, XP, Level, Progress%, NextLevelXP, AvatarColor
        data.add(new LeaderboardItem("1", "Jordan", "Jordan K.", "DE", "9,800 XP", "LVL 15", 82, "12,000 XP", Color.parseColor("#E09F9F")));
        data.add(new LeaderboardItem("2", "Aisha", "Aisha M.", "AE", "8,750 XP", "LVL 14", 88, "10,000 XP", Color.parseColor("#EBBED6")));
        data.add(new LeaderboardItem("3", "Liam", "Liam O.", "IE", "7,420 XP", "LVL 13", 83, "9,000 XP", Color.parseColor("#9B7E51")));
        data.add(new LeaderboardItem("4", "Chloe", "Chloe L.", "FR", "6,900 XP", "LVL 12", 81, "8,500 XP", Color.parseColor("#C9A7A7")));
        data.add(new LeaderboardItem("5", "Sarah", "Sarah J. (You)", "UK", "4,850 XP", "LVL 10", 81, "6,000 XP", Color.parseColor("#A855F7")));

        adapter = new LeaderboardAdapter(data);
        rvLeaderboard.setAdapter(adapter);
    }
}
