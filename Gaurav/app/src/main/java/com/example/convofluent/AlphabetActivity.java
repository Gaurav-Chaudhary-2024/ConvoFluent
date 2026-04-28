package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class AlphabetActivity extends AppCompatActivity {

    private ImageButton btnSearch, btnHelp;
    private Button btnContinue;
    private LinearLayout tabHome, tabLessons, tabAlphabet, tabProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alphabet);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnSearch    = findViewById(R.id.btnSearch);
        btnHelp      = findViewById(R.id.btnHelp);
        btnContinue  = findViewById(R.id.btnContinue);
        tabHome      = findViewById(R.id.tabHome);
        tabLessons   = findViewById(R.id.tabLessons);
        tabAlphabet  = findViewById(R.id.tabAlphabet);
        tabProfile   = findViewById(R.id.tabProfile);
    }

    private void setupClickListeners() {

        btnSearch.setOnClickListener(v ->
                Toast.makeText(this, "Search coming soon!", Toast.LENGTH_SHORT).show());

        btnHelp.setOnClickListener(v ->
                Toast.makeText(this, "Help coming soon!", Toast.LENGTH_SHORT).show());

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScriptLessonActivity.class);
            startActivity(intent);
        });

        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        tabLessons.setOnClickListener(v ->
                Toast.makeText(this, "Lessons coming soon!", Toast.LENGTH_SHORT).show());

        tabAlphabet.setOnClickListener(v ->
                Toast.makeText(this, "You are on Alphabet", Toast.LENGTH_SHORT).show());

        tabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}