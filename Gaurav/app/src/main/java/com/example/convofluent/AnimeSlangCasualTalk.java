package com.example.convofluent;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AnimeSlangCasualTalk extends AppCompatActivity {

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
                .setText("Anime Slang: Casual Talk");
        ((android.widget.TextView) findViewById(R.id.tvTopicTitle))
                .setText("Topic: Casual Japanese");
        ((android.widget.TextView) findViewById(R.id.tvTopicDesc))
                .setText("Learn informal expressions and slang commonly used in anime and everyday casual conversations.");
        ((android.widget.TextView) findViewById(R.id.tvNextTask))
                .setText("Next Task: Respond Casually");

        // Options
        ((android.widget.TextView) findViewById(R.id.tvOption1Japanese)).setText("Sugoi ne!");
        ((android.widget.TextView) findViewById(R.id.tvOption1English)).setText("That's amazing!");
        ((android.widget.TextView) findViewById(R.id.tvOption2Japanese)).setText("Maji de?");
        ((android.widget.TextView) findViewById(R.id.tvOption2English)).setText("Seriously?");
        ((android.widget.TextView) findViewById(R.id.tvOption3Japanese)).setText("Yabai!");
        ((android.widget.TextView) findViewById(R.id.tvOption3English)).setText("Wow / That's crazy!");
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnInfo.setOnClickListener(v ->
                Toast.makeText(this, "Lesson: Anime Slang - Casual Talk", Toast.LENGTH_SHORT).show());

        option1.setOnClickListener(v ->
                Toast.makeText(this, "You selected: Sugoi ne!", Toast.LENGTH_SHORT).show());

        option2.setOnClickListener(v ->
                Toast.makeText(this, "You selected: Maji de?", Toast.LENGTH_SHORT).show());

        option3.setOnClickListener(v ->
                Toast.makeText(this, "You selected: Yabai!", Toast.LENGTH_SHORT).show());

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