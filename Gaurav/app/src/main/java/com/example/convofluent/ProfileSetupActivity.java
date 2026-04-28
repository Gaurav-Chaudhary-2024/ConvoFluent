package com.example.convofluent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class ProfileSetupActivity extends AppCompatActivity {

    // Gender cards
    private LinearLayout cardMale, cardFemale;
    private TextView tvMale, tvFemale;
    private String selectedGender = "";

    // Language cards
    private LinearLayout cardSpanish, cardFrench, cardGerman, cardJapanese;
    private TextView tvSpanish, tvFrench, tvGerman, tvJapanese;
    private String selectedLanguage = "";

    private Button btnFinish;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack      = findViewById(R.id.btnBack);
        btnFinish    = findViewById(R.id.btnFinish);

        cardMale     = findViewById(R.id.cardMale);
        cardFemale   = findViewById(R.id.cardFemale);
        tvMale       = findViewById(R.id.tvMale);
        tvFemale     = findViewById(R.id.tvFemale);

        cardSpanish  = findViewById(R.id.cardSpanish);
        cardFrench   = findViewById(R.id.cardFrench);
        cardGerman   = findViewById(R.id.cardGerman);
        cardJapanese = findViewById(R.id.cardJapanese);
        tvSpanish    = findViewById(R.id.tvSpanish);
        tvFrench     = findViewById(R.id.tvFrench);
        tvGerman     = findViewById(R.id.tvGerman);
        tvJapanese   = findViewById(R.id.tvJapanese);
    }

    private void setupClickListeners() {

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Gender selection
        cardMale.setOnClickListener(v -> selectGender("Male"));
        cardFemale.setOnClickListener(v -> selectGender("Female"));

        // Language selection
        cardSpanish.setOnClickListener(v -> selectLanguage("Spanish"));
        cardFrench.setOnClickListener(v -> selectLanguage("French"));
        cardGerman.setOnClickListener(v -> selectLanguage("German"));
        cardJapanese.setOnClickListener(v -> selectLanguage("Japanese"));

        // Finish button
        btnFinish.setOnClickListener(v -> {
            if (validateForm()) {
                Intent intent = new Intent(this, InterestsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void selectGender(String gender) {
        selectedGender = gender;

        // Reset both cards
        cardMale.setBackgroundResource(R.drawable.gender_card_inactive);
        cardFemale.setBackgroundResource(R.drawable.gender_card_inactive);
        tvMale.setTextColor(getColor(R.color.text_dark));
        tvFemale.setTextColor(getColor(R.color.text_dark));

        // Highlight selected card
        if (gender.equals("Male")) {
            cardMale.setBackgroundResource(R.drawable.gender_card_active);
            tvMale.setTextColor(getColor(R.color.purple_primary));
        } else {
            cardFemale.setBackgroundResource(R.drawable.gender_card_active);
            tvFemale.setTextColor(getColor(R.color.purple_primary));
        }
    }

    private void selectLanguage(String language) {
        selectedLanguage = language;

        // Reset all language cards
        cardSpanish.setBackgroundResource(R.drawable.gender_card_inactive);
        cardFrench.setBackgroundResource(R.drawable.gender_card_inactive);
        cardGerman.setBackgroundResource(R.drawable.gender_card_inactive);
        cardJapanese.setBackgroundResource(R.drawable.gender_card_inactive);
        tvSpanish.setTextColor(getColor(R.color.text_dark));
        tvFrench.setTextColor(getColor(R.color.text_dark));
        tvGerman.setTextColor(getColor(R.color.text_dark));
        tvJapanese.setTextColor(getColor(R.color.text_dark));

        // Highlight selected card
        switch (language) {
            case "Spanish":
                cardSpanish.setBackgroundResource(R.drawable.gender_card_active);
                tvSpanish.setTextColor(getColor(R.color.purple_primary));
                break;
            case "French":
                cardFrench.setBackgroundResource(R.drawable.gender_card_active);
                tvFrench.setTextColor(getColor(R.color.purple_primary));
                break;
            case "German":
                cardGerman.setBackgroundResource(R.drawable.gender_card_active);
                tvGerman.setTextColor(getColor(R.color.purple_primary));
                break;
            case "Japanese":
                cardJapanese.setBackgroundResource(R.drawable.gender_card_active);
                tvJapanese.setTextColor(getColor(R.color.purple_primary));
                break;
        }
    }

    private boolean validateForm() {
        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedLanguage.isEmpty()) {
            Toast.makeText(this, "Please select a language to learn", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}