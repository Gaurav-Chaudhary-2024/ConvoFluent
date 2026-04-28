package com.example.convofluent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class InterestsActivity extends AppCompatActivity {

    // All interest cards
    private LinearLayout cardAnime, cardTravel, cardFood, cardGaming,
            cardTech, cardMusic, cardSports, cardBooks, cardArt, cardMovies;

    // All interest text views
    private TextView tvAnime, tvTravel, tvFood, tvGaming,
            tvTech, tvMusic, tvSports, tvBooks, tvArt, tvMovies;

    // Counter + button
    private TextView tvSelectedCount;
    private Button btnContinue;

    // Track selected interests
    private List<String> selectedInterests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        btnContinue     = findViewById(R.id.btnContinue);

        cardAnime   = findViewById(R.id.cardAnime);
        cardTravel  = findViewById(R.id.cardTravel);
        cardFood    = findViewById(R.id.cardFood);
        cardGaming  = findViewById(R.id.cardGaming);
        cardTech    = findViewById(R.id.cardTech);
        cardMusic   = findViewById(R.id.cardMusic);
        cardSports  = findViewById(R.id.cardSports);
        cardBooks   = findViewById(R.id.cardBooks);
        cardArt     = findViewById(R.id.cardArt);
        cardMovies  = findViewById(R.id.cardMovies);

        tvAnime     = findViewById(R.id.tvAnime);
        tvTravel    = findViewById(R.id.tvTravel);
        tvFood      = findViewById(R.id.tvFood);
        tvGaming    = findViewById(R.id.tvGaming);
        tvTech      = findViewById(R.id.tvTech);
        tvMusic     = findViewById(R.id.tvMusic);
        tvSports    = findViewById(R.id.tvSports);
        tvBooks     = findViewById(R.id.tvBooks);
        tvArt       = findViewById(R.id.tvArt);
        tvMovies    = findViewById(R.id.tvMovies);
    }

    private void setupClickListeners() {
        cardAnime.setOnClickListener(v   -> toggleInterest("Anime & Manga", cardAnime, tvAnime));
        cardTravel.setOnClickListener(v  -> toggleInterest("Travel", cardTravel, tvTravel));
        cardFood.setOnClickListener(v    -> toggleInterest("Food & Dining", cardFood, tvFood));
        cardGaming.setOnClickListener(v  -> toggleInterest("Gaming", cardGaming, tvGaming));
        cardTech.setOnClickListener(v    -> toggleInterest("Technology", cardTech, tvTech));
        cardMusic.setOnClickListener(v   -> toggleInterest("Music", cardMusic, tvMusic));
        cardSports.setOnClickListener(v  -> toggleInterest("Sports", cardSports, tvSports));
        cardBooks.setOnClickListener(v   -> toggleInterest("Books", cardBooks, tvBooks));
        cardArt.setOnClickListener(v     -> toggleInterest("Art & Design", cardArt, tvArt));
        cardMovies.setOnClickListener(v  -> toggleInterest("Movies & TV", cardMovies, tvMovies));

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void toggleInterest(String interest, LinearLayout card, TextView label) {
        if (selectedInterests.contains(interest)) {
            // Deselect
            selectedInterests.remove(interest);
            card.setBackgroundResource(R.drawable.interest_card_inactive);
            label.setTextColor(getColor(R.color.text_dark));
        } else {
            // Select
            selectedInterests.add(interest);
            card.setBackgroundResource(R.drawable.interest_card_active);
            label.setTextColor(getColor(R.color.purple_primary));
        }

        // Update counter
        int count = selectedInterests.size();
        tvSelectedCount.setText(count + " selected");

        // Enable/disable button
        if (count > 0) {
            btnContinue.setEnabled(true);
            btnContinue.setBackgroundResource(R.drawable.btn_primary);
            btnContinue.setText("Continue →");
        } else {
            btnContinue.setEnabled(false);
            btnContinue.setBackgroundResource(R.drawable.btn_disabled);
            btnContinue.setText("Select an Interest");
        }
    }
}