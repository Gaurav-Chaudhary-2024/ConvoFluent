package com.example.convofluent;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrderingAtIzakaya extends AppCompatActivity {

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
        btnBack  = findViewById(R.id.btnBack);
        btnInfo  = findViewById(R.id.btnInfo);
        btnMic   = findViewById(R.id.btnMic);
        btnSend  = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);
        option1  = findViewById(R.id.option1);
        option2  = findViewById(R.id.option2);
        option3  = findViewById(R.id.option3);
    }

    private void setupContent() {
        // Set lesson specific content
        ((android.widget.TextView) findViewById(R.id.tvLessonTitle))
                .setText("Ordering at Izakaya");
        ((android.widget.TextView) findViewById(R.id.tvTopicTitle))
                .setText("Topic: Restaurant Dining");
        ((android.widget.TextView) findViewById(R.id.tvTopicDesc))
                .setText("Practice polite forms (Desu/Masu) while ordering food and asking for recommendations.");
        ((android.widget.TextView) findViewById(R.id.tvNextTask))
                .setText("Next Task: Respond to the Waiter");

        // Options
        ((android.widget.TextView) findViewById(R.id.tvOption1Japanese)).setText("Edamame o kudasai");
        ((android.widget.TextView) findViewById(R.id.tvOption1English)).setText("Order Edamame");
        ((android.widget.TextView) findViewById(R.id.tvOption2Japanese)).setText("Osusume wa nan desu ka?");
        ((android.widget.TextView) findViewById(R.id.tvOption2English)).setText("Ask for Suggestion");
        ((android.widget.TextView) findViewById(R.id.tvOption3Japanese)).setText("Iie, kekkou desu");
        ((android.widget.TextView) findViewById(R.id.tvOption3English)).setText("No, thank you");
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnInfo.setOnClickListener(v ->
                Toast.makeText(this, "Lesson: Ordering at Izakaya", Toast.LENGTH_SHORT).show());

        option1.setOnClickListener(v ->
                Toast.makeText(this, "You selected: Edamame o kudasai", Toast.LENGTH_SHORT).show());

        option2.setOnClickListener(v ->
                Toast.makeText(this, "You selected: Osusume wa nan desu ka?", Toast.LENGTH_SHORT).show());

        option3.setOnClickListener(v ->
                Toast.makeText(this, "You selected: Iie, kekkou desu", Toast.LENGTH_SHORT).show());

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