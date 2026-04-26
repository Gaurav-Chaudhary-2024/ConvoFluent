package com.example.mypage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypage.R;
import com.example.mypage.adapter.ChatAdapter;
import com.example.mypage.model.AppData;
import com.example.mypage.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LessonPlayerActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText     etMessage;
    private ImageButton  btnSend, btnBack, btnMic;

    private final ChatAdapter chatAdapter = new ChatAdapter();
    private ActivityResultLauncher<Intent> voiceLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_player);

        initViews();
        setupVoiceLauncher();
        setupRecycler();
        loadInitialMessages();
        setupListeners();
    }

    private void initViews() {
        recyclerChat = findViewById(R.id.recyclerChat);
        etMessage    = findViewById(R.id.etMessage);
        btnSend      = findViewById(R.id.btnSend);
        btnBack      = findViewById(R.id.btnBack);
        btnMic       = findViewById(R.id.btnMic);
    }

    private void setupVoiceLauncher() {
        voiceLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData()
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty()) {
                        String spokenText = matches.get(0);
                        etMessage.setText(spokenText);
                        etMessage.setSelection(spokenText.length());
                        sendUserMessage();
                    }
                } else {
                    Toast.makeText(this,
                            "Voice not recognized. Try again!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        
        // Use device default language tag (e.g., "en-US") for better recognition accuracy
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        try {
            voiceLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Voice input not supported on this device!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);
    }

    private void loadInitialMessages() {
        chatAdapter.setMessages(AppData.getSampleChatMessages());
        scrollToBottom();
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendUserMessage());
        btnMic.setOnClickListener(v -> startVoiceInput());
        btnBack.setOnClickListener(v -> finish());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendUserMessage();
                return true;
            }
            return false;
        });
    }

    private void sendUserMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        chatAdapter.addMessage(new ChatMessage(text, ChatMessage.Sender.USER, time));
        etMessage.setText("");
        scrollToBottom();

        String finalTime = time;
        recyclerChat.postDelayed(() -> {
            chatAdapter.addMessage(new ChatMessage(
                    generateAiReply(text), ChatMessage.Sender.AI, finalTime));
            scrollToBottom();
        }, 800);
    }

    private String generateAiReply(String userInput) {
        String lower = userInput.toLowerCase();
        if (lower.contains("biiru") || lower.contains("beer") || lower.contains("ビール")) {
            return "Kanpai! 🍺 'Biiru' (ビール) means beer. Would you like to practice ordering food next?";
        } else if (lower.contains("kudasai") || lower.contains("ください")) {
            return "Great use of 'kudasai'! It's a polite way to say 'please give me'. Try asking for gyoza next!";
        } else if (lower.contains("sumimasen") || lower.contains("すみません")) {
            return "'Sumimasen' is perfect for getting attention politely. Well done!";
        } else if (lower.contains("arigato") || lower.contains("ありがとう")) {
            return "Do itashimashite! (You're welcome!) Your Japanese is improving!";
        } else if (lower.contains("konnichiwa") || lower.contains("こんにちは")) {
            return "Konnichiwa! Great greeting! Now try ordering something from the menu.";
        } else {
            return "Ii desu ne! Keep practicing. Try ordering using 'hitotsu kudasai'.";
        }
    }

    private void scrollToBottom() {
        recyclerChat.post(() ->
                recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1));
    }

    public static void start(android.content.Context context) {
        context.startActivity(new Intent(context, LessonPlayerActivity.class));
    }
}
