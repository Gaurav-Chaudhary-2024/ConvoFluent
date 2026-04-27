package com.example.convofluent;

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

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView rvReview;
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnFinish).setOnClickListener(v -> finish());

        initRecyclerView();
    }

    private void initRecyclerView() {
        rvReview = findViewById(R.id.rvReview);
        if (rvReview != null) {
            rvReview.setLayoutManager(new LinearLayoutManager(this));

            List<ReviewItem> data = new ArrayList<>();
            data.add(new ReviewItem(
                    "GRAMMAR",
                    "\"Transitive: I am going to the store.\"",
                    "Je suis aller à le magasin.",
                    "Je vais au magasin.",
                    "Tip: You used 'être' instead of 'aller' and forgot the contraction 'au' (à + le)."
            ));
            data.add(new ReviewItem(
                    "PRONUNCIATION",
                    "\"Pronounce: 'l'ordinateur'\"",
                    "L-or-di-nay-ter",
                    "Lohr-dee-nah-tuhr",
                    "Tip: The 'eu' sound in French is more closed than the English 'er'. Keep it rounded."
            ));

            adapter = new ReviewAdapter(data);
            rvReview.setAdapter(adapter);
        }
    }
}
