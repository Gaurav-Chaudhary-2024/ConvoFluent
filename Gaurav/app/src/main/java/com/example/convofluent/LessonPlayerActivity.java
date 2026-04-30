package com.example.convofluent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class LessonPlayerActivity extends AppCompatActivity {

    protected ImageButton  btnBack, btnInfo, btnMic, btnSend;
    protected EditText     etMessage;
    protected LinearLayout option1, option2, option3;
    protected LinearLayout chatContainer;
    protected ScrollView   scrollView;

    protected DatabaseHelper db;
    protected long userId;
    protected abstract String getLessonName();

    private static final String GEMINI_API_KEY = "";
    private static final String GEMINI_MODEL = "gemini-2.0-flash-lite";
    private static final String GEMINI_URL     =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + GEMINI_MODEL + ":generateContent?key=" + GEMINI_API_KEY;

    private final List<JSONObject> conversationHistory = new ArrayList<>();
    private int messageCount = 0;

    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private static final int MIC_PERMISSION_CODE = 101;

    protected abstract String getSystemPrompt();
    protected abstract String getOpeningMessage();
    protected abstract void   onInfoClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_player);
        db     = DatabaseHelper.getInstance(this);
        userId = SessionManager.getUserId(this);
        initBaseViews();
        setupBaseClickListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) speechRecognizer.destroy();
    }

    private void initBaseViews() {
        btnBack       = findViewById(R.id.btnBack);
        btnInfo       = findViewById(R.id.btnInfo);
        btnMic        = findViewById(R.id.btnMic);
        btnSend       = findViewById(R.id.btnSend);
        etMessage     = findViewById(R.id.etMessage);
        option1       = findViewById(R.id.option1);
        option2       = findViewById(R.id.option2);
        option3       = findViewById(R.id.option3);
        chatContainer = findViewById(R.id.chatContainer);
        scrollView    = (ScrollView) chatContainer.getParent().getParent();
        chatContainer.removeAllViews();
    }

    protected void startConversation() {
        List<DatabaseHelper.ChatMessage> history = db.getChatHistory(userId, getLessonName());
        if (history.isEmpty()) {
            addAiBubble(getOpeningMessage());
        } else {
            for (DatabaseHelper.ChatMessage m : history) {
                if (m.role.equals("user")) addUserBubble(m.message);
                else addAiBubble(m.message);
                addToHistory(m.role, m.message);
                messageCount++;
            }
            updateProgressBar();
        }
    }

    private void setupBaseClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnInfo.setOnClickListener(v -> onInfoClick());
        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) { etMessage.setText(""); sendUserMessage(text); }
        });
        btnMic.setOnClickListener(v -> toggleVoiceInput());
        option1.setOnClickListener(v -> sendUserMessage(
                ((TextView) findViewById(R.id.tvOption1Japanese)).getText().toString()));
        option2.setOnClickListener(v -> sendUserMessage(
                ((TextView) findViewById(R.id.tvOption2Japanese)).getText().toString()));
        option3.setOnClickListener(v -> sendUserMessage(
                ((TextView) findViewById(R.id.tvOption3Japanese)).getText().toString()));
    }

    protected void sendUserMessage(String text) {
        addUserBubble(text);
        addToHistory("user", text);
        db.saveChatMessage(userId, getLessonName(), "user", text);
        showTypingIndicator();
        messageCount++;
        updateProgressBar();
        callGeminiApi();
    }

    private void updateProgressBar() {
        int progress = Math.min(messageCount * 10, 100);
        db.saveLessonProgress(userId, getLessonName(), progress);
        if (messageCount % 2 == 0) db.addXp(userId, 10);
        View pf = findViewById(R.id.progressFill);
        TextView tvP = findViewById(R.id.tvProgress);
        if (pf != null) pf.post(() -> {
            pf.getLayoutParams().width = (int)(((View)pf.getParent()).getWidth() * (progress/100f));
            pf.requestLayout();
        });
        if (tvP != null) tvP.setText(progress + "%");
    }

    protected void addAiBubble(String text) {
        runOnUiThread(() -> {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rp.setMargins(0,0,0,dp(12)); row.setLayoutParams(rp);

            TextView av = new TextView(this);
            LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(dp(32),dp(32));
            ap.setMargins(0,0,dp(8),0); av.setLayoutParams(ap);
            av.setText("AI"); av.setTextSize(10f); av.setTextColor(0xFFFFFFFF);
            av.setTypeface(null, Typeface.BOLD); av.setGravity(Gravity.CENTER);
            av.setBackgroundResource(R.drawable.avatar_background); row.addView(av);

            TextView b = new TextView(this);
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f);
            bp.setMargins(0,0,dp(40),0); b.setLayoutParams(bp);
            b.setText(text); b.setTextSize(14f); b.setTextColor(0xFF1A1A2E);
            b.setBackgroundResource(R.drawable.bubble_ai_background);
            b.setPadding(dp(14),dp(10),dp(14),dp(10)); row.addView(b);

            chatContainer.addView(row); scrollToBottom();
        });
    }

    private void addUserBubble(String text) {
        runOnUiThread(() -> {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rp.setMargins(0,0,0,dp(12)); row.setLayoutParams(rp);

            TextView b = new TextView(this);
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            bp.setMargins(dp(40),0,0,0); b.setLayoutParams(bp);
            b.setText(text); b.setTextSize(14f); b.setTextColor(0xFFFFFFFF);
            b.setBackgroundResource(R.drawable.bubble_user_background);
            b.setPadding(dp(14),dp(10),dp(14),dp(10)); row.addView(b);

            chatContainer.addView(row); scrollToBottom();
        });
    }

    private View typingView;
    private void showTypingIndicator() {
        runOnUiThread(() -> {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rp.setMargins(0,0,0,dp(12)); row.setLayoutParams(rp);
            TextView av = new TextView(this);
            LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(dp(32),dp(32));
            ap.setMargins(0,0,dp(8),0); av.setLayoutParams(ap);
            av.setText("AI"); av.setTextSize(10f); av.setTextColor(0xFFFFFFFF);
            av.setTypeface(null,Typeface.BOLD); av.setGravity(Gravity.CENTER);
            av.setBackgroundResource(R.drawable.avatar_background); row.addView(av);
            TextView t = new TextView(this);
            t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            t.setText("● ● ●"); t.setTextSize(10f); t.setTextColor(0xFF9999AA);
            t.setBackgroundResource(R.drawable.bubble_ai_background);
            t.setPadding(dp(14),dp(10),dp(14),dp(10)); row.addView(t);
            typingView = row; chatContainer.addView(row); scrollToBottom();
        });
    }
    private void removeTypingIndicator() {
        runOnUiThread(() -> { if(typingView!=null){chatContainer.removeView(typingView);typingView=null;} });
    }

    private void addToHistory(String role, String content) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("role", role.equals("user") ? "user" : "model");
            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("text", content));
            msg.put("parts", parts);
            conversationHistory.add(msg);
        } catch (Exception ignored) {}
    }

    private void callGeminiApi() {
        new Thread(() -> {
            try {
                JSONObject si = new JSONObject();
                JSONArray sp = new JSONArray();
                sp.put(new JSONObject().put("text", getSystemPrompt()));
                si.put("parts", sp);
                List<JSONObject> trimmedHistory = conversationHistory.size() > 6
                        ? conversationHistory.subList(conversationHistory.size() - 6, conversationHistory.size())
                        : conversationHistory;
                JSONArray contents = new JSONArray();
                for (JSONObject m : trimmedHistory) contents.put(m);
                JSONObject body = new JSONObject();
                body.put("system_instruction", si);
                body.put("contents", contents);
                body.put("generationConfig", new JSONObject().put("maxOutputTokens", 300));

                URL url = new URL(GEMINI_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                int code = conn.getResponseCode();
                java.io.InputStream is = code==200 ? conn.getInputStream() : conn.getErrorStream();
                String raw = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                is.close();

                android.util.Log.e("GEMINI_ERROR", "HTTP code: " + code + " | body: " + raw);

                removeTypingIndicator();

                if (code == 200) {
                    String reply = new JSONObject(raw)
                            .getJSONArray("candidates").getJSONObject(0)
                            .getJSONObject("content").getJSONArray("parts")
                            .getJSONObject(0).getString("text");
                    addAiBubble(reply);
                    addToHistory("model", reply);
                    db.saveChatMessage(userId, getLessonName(), "model", reply);
                } else if (code == 429) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        showTypingIndicator();
                        callGeminiApi();
                    }, 60000);
                } else {
                    addAiBubble("(Sorry, couldn't connect. Try again!)");
                }
            } catch (Exception e) {
                removeTypingIndicator();
                addAiBubble("(Connection error — check your internet.)");
            }
        }).start();
    }

    private void toggleVoiceInput() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_CODE);
            return;
        }
        if (isListening) stopListening(); else startListening();
    }

    private void startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this,"Speech recognition not available",Toast.LENGTH_SHORT).show(); return;
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle p) {
                isListening = true;
                runOnUiThread(() -> { btnMic.setColorFilter(0xFFE53935); etMessage.setHint("Listening…"); });
            }
            @Override public void onResults(Bundle r) {
                ArrayList<String> m = r.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (m!=null&&!m.isEmpty()) runOnUiThread(()->etMessage.setText(m.get(0)));
                stopListening();
            }
            @Override public void onError(int e) {
                runOnUiThread(()->Toast.makeText(LessonPlayerActivity.this,"Couldn't hear — try again",Toast.LENGTH_SHORT).show());
                stopListening();
            }
            @Override public void onBeginningOfSpeech(){}
            @Override public void onBufferReceived(byte[] b){}
            @Override public void onEndOfSpeech(){}
            @Override public void onEvent(int t,Bundle b){}
            @Override public void onPartialResults(Bundle b){}
            @Override public void onRmsChanged(float v){}
        });
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(i);
    }

    private void stopListening() {
        isListening = false;
        runOnUiThread(() -> { btnMic.clearColorFilter(); etMessage.setHint("Or type your own response..."); });
        if (speechRecognizer != null) speechRecognizer.stopListening();
    }

    @Override
    public void onRequestPermissionsResult(int code,@NonNull String[] p,@NonNull int[] r) {
        super.onRequestPermissionsResult(code,p,r);
        if(code==MIC_PERMISSION_CODE&&r.length>0&&r[0]==PackageManager.PERMISSION_GRANTED) startListening();
        else Toast.makeText(this,"Microphone permission denied",Toast.LENGTH_SHORT).show();
    }

    private void scrollToBottom() {
        new Handler(Looper.getMainLooper()).postDelayed(()->scrollView.fullScroll(ScrollView.FOCUS_DOWN),100);
    }
    protected int dp(int v) { return Math.round(v*getResources().getDisplayMetrics().density); }
}
