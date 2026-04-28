package com.example.convofluent;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DailyExpressionsCommuting extends AppCompatActivity {

    private ImageButton btnBack, btnInfo, btnMic, btnSend;
    private EditText etMessage;
    private LinearLayout option1, option2, option3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_player);

        initViews();
        setupContent();
        setupClickListeners();
    }

    private void initViews() {
        btnBack   = findViewById(R.id.btnBack);
        btnInfo   = findViewById(R.id.btnInfo);
        btnMic    = findViewById(R.id.btnMic);
        btnSend   = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);
        option1   = findViewById(R.id.option1);
        option2   = findViewById(R.id.option2);
        option3   = findViewById(R.id.option3);
    }

    private void setupContent() {
        ((android.widget.TextView) findViewById(R.id.tvLessonTitle))
                .setText("Daily Expressions: Commuting");
        ((android.widget.TextView) findViewById(R.id.tvTopicTitle))
                .setText("Topic: Public Transport");
        ((android.widget.TextView) findViewById(R.id.tvTopicDesc))
                .setText("Learn essential phrases for navigating trains, buses, and stations in Japan.");
        ((android.widget.TextView) findViewById(R.id.tvNextTask))
                .setText("Next Task: Ask for Directions");

        // Options
        ((android.widget.TextView) findViewById(R.id.tvOption1Japanese)).setText("Sumimasen, eki wa doko desu ka?");
        ((android.widget.TextView) findViewById(R.id.tvOption1English)).setText("Excuse me, where is the station?");
        ((android.widget.TextView) findViewById(R.id.tvOption2Japanese)).setText("Kono densha wa Shinjuku ni tomarimasu ka?");
        ((android.widget.TextView) findViewById(R.id.tvOption2English)).setText("Does this train stop at Shinjuku?");
        ((android.widget.TextView) findViewById(R.id.tvOption3Japanese)).setText("Kippu o ichi-mai kudasai");
        ((android.widget.TextView) findViewById(R.id.tvOption3English)).setText("One ticket, please");
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnInfo.setOnClickListener(v ->
                Toast.makeText(this, "Lesson: Daily Expressions - Commuting", Toast.LENGTH_SHORT).show());

        option1.setOnClickListener(v ->
                Toast.makeText(this, "You selected: Sumimasen, eki wa doko desu ka?", Toast.LENGTH_SHORT).show());

        option2.setOnClickListener(v ->
                Toast.makeText(this, "You selected: Kono densha wa Shinjuku ni tomarimasu ka?", Toast.LENGTH_SHORT).show());

        option3.setOnClickListener(v ->
                Toast.makeText(this, "You selected: Kippu o ichi-mai kudasai", Toast.LENGTH_SHORT).show());

        btnMic.setOnClickListener(v ->
                Toast.makeText(this, "Voice input coming soon!", Toast.LENGTH_SHORT).show());

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                Toast.makeText(this, "Sent: " + message, Toast.LENGTH_SHORT).show();
                etMessage.setText("");
            }
        });
    }
}