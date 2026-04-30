package com.example.convofluent;

import android.os.Bundle;
import android.widget.Toast;

public class AnimeSlangCasualTalk extends LessonPlayerActivity {

    @Override protected String getLessonName() { return "AnimeSlangCasualTalk"; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupContent();
        startConversation();
    }

    private void setupContent() {
        ((android.widget.TextView) findViewById(R.id.tvLessonTitle)).setText("Anime Slang: Casual Talk");
        ((android.widget.TextView) findViewById(R.id.tvTopicTitle)).setText("Topic: Casual Japanese");
        ((android.widget.TextView) findViewById(R.id.tvTopicDesc)).setText(
                "Learn informal expressions and slang commonly used in anime and everyday casual conversations.");
        ((android.widget.TextView) findViewById(R.id.tvNextTask)).setText("Next Task: Respond Casually");
        ((android.widget.TextView) findViewById(R.id.tvOption1Japanese)).setText("Sugoi ne!");
        ((android.widget.TextView) findViewById(R.id.tvOption1English)).setText("That's amazing!");
        ((android.widget.TextView) findViewById(R.id.tvOption2Japanese)).setText("Maji de?");
        ((android.widget.TextView) findViewById(R.id.tvOption2English)).setText("Seriously?");
        ((android.widget.TextView) findViewById(R.id.tvOption3Japanese)).setText("Yabai!");
        ((android.widget.TextView) findViewById(R.id.tvOption3English)).setText("Wow / That's crazy!");
    }

    @Override
    protected String getSystemPrompt() {
        return "You are a fun, enthusiastic Japanese language tutor running a casual anime slang lesson. " +
                "You play the role of a Japanese high school student who loves anime. " +
                "Have a casual conversation using common anime slang and everyday expressions. " +
                "When the student uses Japanese slang, react with excitement, confirm correct usage, " +
                "gently correct mistakes, and keep the chat going. " +
                "If they respond in English, encourage them to try the Japanese version. " +
                "Keep replies SHORT — 2-4 sentences max. " +
                "Always include a slang phrase with its meaning in parentheses. Keep it fun!";
    }

    @Override
    protected String getOpeningMessage() {
        return "Yoo! 👋 Hajimemashite~ Nice to meet you!\n\n" +
                "I'm Hiro, obsessed with anime. Let's chat in casual Japanese!\n\n" +
                "Kick things off with \"Sugoi ne!\" (Amazing, right?!) " +
                "or tap one of the options below. Let's gooo! 🎌";
    }

    @Override
    protected void onInfoClick() {
        Toast.makeText(this, "Lesson: Anime Slang - Casual Talk", Toast.LENGTH_SHORT).show();
    }
}