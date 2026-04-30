package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {

    private LinearLayout cardAnime, cardTravel, cardFood, cardGaming,
            cardTech, cardMusic, cardSports, cardBooks, cardArt, cardMovies;
    private TextView tvAnime, tvTravel, tvFood, tvGaming,
            tvTech, tvMusic, tvSports, tvBooks, tvArt, tvMovies;
    private TextView tvSelectedCount;
    private Button   btnContinue;

    private final List<String> selectedInterests = new ArrayList<>();
    private DatabaseHelper db;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        db     = DatabaseHelper.getInstance(this);
        userId = SessionManager.getUserId(this);

        initViews();
        preFillSavedInterests();
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

    /** Load previously saved interests and highlight their cards. */
    private void preFillSavedInterests() {
        List<String> saved = db.getInterests(userId);
        for (String interest : saved) {
            switch (interest) {
                case "Anime & Manga": activateCard(interest, cardAnime,  tvAnime);  break;
                case "Travel":        activateCard(interest, cardTravel, tvTravel); break;
                case "Food & Dining": activateCard(interest, cardFood,   tvFood);   break;
                case "Gaming":        activateCard(interest, cardGaming, tvGaming); break;
                case "Technology":    activateCard(interest, cardTech,   tvTech);   break;
                case "Music":         activateCard(interest, cardMusic,  tvMusic);  break;
                case "Sports":        activateCard(interest, cardSports, tvSports); break;
                case "Books":         activateCard(interest, cardBooks,  tvBooks);  break;
                case "Art & Design":  activateCard(interest, cardArt,    tvArt);    break;
                case "Movies & TV":   activateCard(interest, cardMovies, tvMovies); break;
            }
        }
        updateCounter();
    }

    private void activateCard(String interest, LinearLayout card, TextView label) {
        selectedInterests.add(interest);
        card.setBackgroundResource(R.drawable.interest_card_active);
        label.setTextColor(getColor(R.color.purple_primary));
    }

    private void setupClickListeners() {
        cardAnime.setOnClickListener(v   -> toggleInterest("Anime & Manga", cardAnime,  tvAnime));
        cardTravel.setOnClickListener(v  -> toggleInterest("Travel",        cardTravel, tvTravel));
        cardFood.setOnClickListener(v    -> toggleInterest("Food & Dining", cardFood,   tvFood));
        cardGaming.setOnClickListener(v  -> toggleInterest("Gaming",        cardGaming, tvGaming));
        cardTech.setOnClickListener(v    -> toggleInterest("Technology",    cardTech,   tvTech));
        cardMusic.setOnClickListener(v   -> toggleInterest("Music",         cardMusic,  tvMusic));
        cardSports.setOnClickListener(v  -> toggleInterest("Sports",        cardSports, tvSports));
        cardBooks.setOnClickListener(v   -> toggleInterest("Books",         cardBooks,  tvBooks));
        cardArt.setOnClickListener(v     -> toggleInterest("Art & Design",  cardArt,    tvArt));
        cardMovies.setOnClickListener(v  -> toggleInterest("Movies & TV",   cardMovies, tvMovies));

        btnContinue.setOnClickListener(v -> {
            // Save to DB then go to Home
            db.saveInterests(userId, selectedInterests);
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void toggleInterest(String interest, LinearLayout card, TextView label) {
        if (selectedInterests.contains(interest)) {
            selectedInterests.remove(interest);
            card.setBackgroundResource(R.drawable.interest_card_inactive);
            label.setTextColor(getColor(R.color.text_dark));
        } else {
            selectedInterests.add(interest);
            card.setBackgroundResource(R.drawable.interest_card_active);
            label.setTextColor(getColor(R.color.purple_primary));
        }
        updateCounter();
    }

    private void updateCounter() {
        int count = selectedInterests.size();
        tvSelectedCount.setText(count + " selected");
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