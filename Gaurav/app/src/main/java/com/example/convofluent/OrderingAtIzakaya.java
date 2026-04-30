package com.example.convofluent;

import android.os.Bundle;
import android.widget.Toast;

public class OrderingAtIzakaya extends LessonPlayerActivity {

    @Override protected String getLessonName() { return "OrderingAtIzakaya"; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupContent();
        startConversation();
    }

    private void setupContent() {
        ((android.widget.TextView) findViewById(R.id.tvLessonTitle)).setText("Ordering at Izakaya");
        ((android.widget.TextView) findViewById(R.id.tvTopicTitle)).setText("Topic: Restaurant Dining");
        ((android.widget.TextView) findViewById(R.id.tvTopicDesc)).setText(
                "Practice polite forms (Desu/Masu) while ordering food and asking for recommendations.");
        ((android.widget.TextView) findViewById(R.id.tvNextTask)).setText("Next Task: Respond to the Waiter");
        ((android.widget.TextView) findViewById(R.id.tvOption1Japanese)).setText("Edamame o kudasai");
        ((android.widget.TextView) findViewById(R.id.tvOption1English)).setText("Order Edamame");
        ((android.widget.TextView) findViewById(R.id.tvOption2Japanese)).setText("Osusume wa nan desu ka?");
        ((android.widget.TextView) findViewById(R.id.tvOption2English)).setText("Ask for Suggestion");
        ((android.widget.TextView) findViewById(R.id.tvOption3Japanese)).setText("Iie, kekkou desu");
        ((android.widget.TextView) findViewById(R.id.tvOption3English)).setText("No, thank you");
    }

    @Override
    protected String getSystemPrompt() {
        return "You are a friendly Japanese language tutor running an izakaya role-play lesson. " +
                "You play the role of a waiter at a busy Tokyo izakaya. " +
                "When the student responds in Japanese (even romaji), praise them briefly, " +
                "correct any mistakes gently, then continue the conversation naturally. " +
                "If they respond in English, encourage them to try in Japanese and give a hint. " +
                "Keep each reply SHORT — 2-4 sentences max. " +
                "Always include a Japanese phrase with its English meaning in parentheses. " +
                "Stay in character and make the lesson fun.";
    }

    @Override
    protected String getOpeningMessage() {
        return "Irasshaimase! 🏮 Welcome to Sakura Izakaya!\n\n" +
                "I'm your waiter today. Let's practice ordering in Japanese!\n\n" +
                "Try: \"Edamame o kudasai\" (Edamame, please) " +
                "or tap one of the response options below!";
    }

    @Override
    protected void onInfoClick() {
        Toast.makeText(this, "Lesson: Ordering at Izakaya", Toast.LENGTH_SHORT).show();
    }
}