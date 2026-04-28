package com.example.convofluent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ScriptLessonActivity extends AppCompatActivity {

    private ImageButton btnBack, btnReference, btnPlayUsage;
    private Button btnListen, btnTraceChar, btnNextChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_lesson);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack       = findViewById(R.id.btnBack);
        btnReference  = findViewById(R.id.btnReference);
        btnPlayUsage  = findViewById(R.id.btnPlayUsage);
        btnListen     = findViewById(R.id.btnListen);
        btnTraceChar  = findViewById(R.id.btnTraceChar);
        btnNextChar   = findViewById(R.id.btnNextChar);
    }

    private void setupClickListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnReference.setOnClickListener(v ->
                Toast.makeText(this, "Reference guide coming soon!", Toast.LENGTH_SHORT).show());

        btnListen.setOnClickListener(v ->
                Toast.makeText(this, "Playing: あ sounds like 'a' in Father", Toast.LENGTH_SHORT).show());

        btnPlayUsage.setOnClickListener(v ->
                Toast.makeText(this, "Playing: あき (AKI) - Autumn", Toast.LENGTH_SHORT).show());

        btnTraceChar.setOnClickListener(v ->
                Toast.makeText(this, "Trace mode coming soon!", Toast.LENGTH_SHORT).show());

        btnNextChar.setOnClickListener(v ->
                Toast.makeText(this, "Moving to next character: い", Toast.LENGTH_SHORT).show());
    }
}
