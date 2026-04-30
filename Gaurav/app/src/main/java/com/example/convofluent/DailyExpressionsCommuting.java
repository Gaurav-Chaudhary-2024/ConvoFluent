package com.example.convofluent;

import android.os.Bundle;
import android.widget.Toast;

public class DailyExpressionsCommuting extends LessonPlayerActivity {

    @Override protected String getLessonName() { return "DailyExpressionsCommuting"; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupContent();
        startConversation();
    }

    private void setupContent() {
        ((android.widget.TextView) findViewById(R.id.tvLessonTitle)).setText("Daily Expressions: Commuting");
        ((android.widget.TextView) findViewById(R.id.tvTopicTitle)).setText("Topic: Public Transport");
        ((android.widget.TextView) findViewById(R.id.tvTopicDesc)).setText(
                "Learn essential phrases for navigating trains, buses, and stations in Japan.");
        ((android.widget.TextView) findViewById(R.id.tvNextTask)).setText("Next Task: Ask for Directions");
        ((android.widget.TextView) findViewById(R.id.tvOption1Japanese)).setText("Sumimasen, eki wa doko desu ka?");
        ((android.widget.TextView) findViewById(R.id.tvOption1English)).setText("Where is the station?");
        ((android.widget.TextView) findViewById(R.id.tvOption2Japanese)).setText("Kono densha wa Shinjuku ni tomarimasu ka?");
        ((android.widget.TextView) findViewById(R.id.tvOption2English)).setText("Does this stop at Shinjuku?");
        ((android.widget.TextView) findViewById(R.id.tvOption3Japanese)).setText("Kippu o ichi-mai kudasai");
        ((android.widget.TextView) findViewById(R.id.tvOption3English)).setText("One ticket, please");
    }

    @Override
    protected String getSystemPrompt() {
        return "You are a friendly Japanese language tutor running a commuting role-play lesson. " +
                "You play different characters: a station attendant, a fellow passenger, or a ticket machine prompt. " +
                "When the student uses Japanese (even romaji), praise them briefly, correct mistakes gently, " +
                "and continue the scenario. If they respond in English, encourage them to try Japanese. " +
                "Keep replies SHORT — 2-4 sentences max. " +
                "Always include a Japanese phrase with its English meaning in parentheses. " +
                "Make the scenario feel like a real Tokyo train station.";
    }

    @Override
    protected String getOpeningMessage() {
        return "🚆 Welcome to Shinjuku Station — one of the world's busiest!\n\n" +
                "I'm a station attendant. You look a little lost — let's practice asking for help in Japanese!\n\n" +
                "Try: \"Sumimasen, eki wa doko desu ka?\" (Excuse me, where is the station?) " +
                "or tap an option below!";
    }

    @Override
    protected void onInfoClick() {
        Toast.makeText(this, "Lesson: Daily Expressions - Commuting", Toast.LENGTH_SHORT).show();
    }
}