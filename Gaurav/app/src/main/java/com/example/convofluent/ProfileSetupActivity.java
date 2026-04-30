package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileSetupActivity extends AppCompatActivity {

    private LinearLayout cardMale, cardFemale;
    private TextView     tvMale, tvFemale;
    private String       selectedGender = "";

    private LinearLayout cardSpanish, cardFrench, cardGerman, cardJapanese;
    private TextView     tvSpanish, tvFrench, tvGerman, tvJapanese;
    private String       selectedLanguage = "";

    private Button      btnFinish;
    private ImageButton btnBack;

    // Registration data passed from RegisterActivity
    private String fullName, username, email, password;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        db = DatabaseHelper.getInstance(this);

        // Receive data from RegisterActivity
        fullName = getIntent().getStringExtra("full_name");
        username = getIntent().getStringExtra("username");
        email    = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        // If coming from ProfileActivity (edit mode), pre-fill from DB
        if (fullName == null && SessionManager.isLoggedIn(this)) {
            long userId = SessionManager.getUserId(this);
            DatabaseHelper.User user = db.getUser(userId);
            if (user != null) {
                selectedGender   = user.gender   != null ? user.gender   : "";
                selectedLanguage = user.language != null ? user.language : "";
                if (!selectedGender.isEmpty())   selectGender(selectedGender);
                if (!selectedLanguage.isEmpty()) selectLanguage(selectedLanguage);
            }
        }

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
        btnBack.setOnClickListener(v -> finish());

        cardMale.setOnClickListener(v    -> selectGender("Male"));
        cardFemale.setOnClickListener(v  -> selectGender("Female"));
        cardSpanish.setOnClickListener(v -> selectLanguage("Spanish"));
        cardFrench.setOnClickListener(v  -> selectLanguage("French"));
        cardGerman.setOnClickListener(v  -> selectLanguage("German"));
        cardJapanese.setOnClickListener(v-> selectLanguage("Japanese"));

        btnFinish.setOnClickListener(v -> {
            if (!validateForm()) return;

            if (fullName != null) {
                // ── New registration flow ──────────────────────────────
                long userId = db.registerUser(fullName, username, email,
                        password, selectedGender, selectedLanguage);
                if (userId == -1) {
                    Toast.makeText(this, "Registration failed. Email or username already taken.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                SessionManager.login(this, userId);
                startActivity(new Intent(this, InterestsActivity.class));
            } else {
                // ── Edit profile flow (existing user) ──────────────────
                long userId = SessionManager.getUserId(this);
                db.updateUserProfile(userId, selectedGender, selectedLanguage);
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void selectGender(String gender) {
        selectedGender = gender;
        cardMale.setBackgroundResource(R.drawable.gender_card_inactive);
        cardFemale.setBackgroundResource(R.drawable.gender_card_inactive);
        tvMale.setTextColor(getColor(R.color.text_dark));
        tvFemale.setTextColor(getColor(R.color.text_dark));
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
        cardSpanish.setBackgroundResource(R.drawable.gender_card_inactive);
        cardFrench.setBackgroundResource(R.drawable.gender_card_inactive);
        cardGerman.setBackgroundResource(R.drawable.gender_card_inactive);
        cardJapanese.setBackgroundResource(R.drawable.gender_card_inactive);
        tvSpanish.setTextColor(getColor(R.color.text_dark));
        tvFrench.setTextColor(getColor(R.color.text_dark));
        tvGerman.setTextColor(getColor(R.color.text_dark));
        tvJapanese.setTextColor(getColor(R.color.text_dark));
        switch (language) {
            case "Spanish":
                cardSpanish.setBackgroundResource(R.drawable.gender_card_active);
                tvSpanish.setTextColor(getColor(R.color.purple_primary)); break;
            case "French":
                cardFrench.setBackgroundResource(R.drawable.gender_card_active);
                tvFrench.setTextColor(getColor(R.color.purple_primary)); break;
            case "German":
                cardGerman.setBackgroundResource(R.drawable.gender_card_active);
                tvGerman.setTextColor(getColor(R.color.purple_primary)); break;
            case "Japanese":
                cardJapanese.setBackgroundResource(R.drawable.gender_card_active);
                tvJapanese.setTextColor(getColor(R.color.purple_primary)); break;
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