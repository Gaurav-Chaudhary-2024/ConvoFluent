package com.example.convofluent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TestScreenActivity extends AppCompatActivity {

    private ImageButton  btnBack, btnReplay;
    private LinearLayout btnPlay;
    private LinearLayout optionA, optionB, optionC, optionD;
    private LinearLayout btnSkip, btnCheckAnswer;
    private TextView     tvCheckAnswer, tvTimer, tvQuestionCounter, tvScore;
    private TextView     tvQuestionType, tvQuestionPoints;
    private TextView     tvOptionA, tvOptionB, tvOptionC, tvOptionD;
    private TextView     tvQuestionText, tvQuestionSub;
    private ProgressBar  progressBar;

    private CountDownTimer countDownTimer;
    private int selectedOption  = -1;
    private int currentQuestion = 0;
    private int score           = 0;
    private int secondsRemaining = 299;
    private final int[] userAnswers = new int[10];

    private DatabaseHelper db;
    private long userId;

    private static final String[][] QUESTIONS = {
            {"Listening","100","Listen and select the correct translation for \"Sumimasen\".","Tap play to hear the audio.","I'm sorry / Excuse me","Thank you very much","See you later","Good evening","0"},
            {"Grammar","100","Which particle completes: \"Watashi ___ gakusei desu.\"","Choose the correct particle.","wa (は)","ga (が)","ni (に)","wo (を)","0"},
            {"Vocabulary","100","What does \"Oishii\" (おいしい) mean?","Select the correct meaning.","Beautiful","Delicious","Expensive","Quiet","1"},
            {"Speaking","100","How do you say \"Good morning\" in Japanese?","Choose the correct greeting.","Konbanwa","Konnichiwa","Ohayou gozaimasu","Sayonara","2"},
            {"Reading","100","What is the correct reading of the kanji \"水\"?","Select the correct pronunciation.","Hi (fire)","Mizu (water)","Kaze (wind)","Tsuchi (earth)","1"},
            {"Grammar","100","Which sentence means \"I go to school\"?","Choose the correct option.","Gakkou ga ikimasu","Gakkou wo ikimasu","Gakkou ni ikimasu","Gakkou de ikimasu","2"},
            {"Vocabulary","100","What does \"Muzukashii\" (むずかしい) mean?","Select the correct meaning.","Easy","Interesting","Difficult","Boring","2"},
            {"Listening","100","Which phrase means \"Where is the station?\"","Tap play to hear the audio.","Eki wa doko desu ka?","Eki ga suki desu","Eki ni ikimasu","Eki wa nani desu ka?","0"},
            {"Reading","100","What does the hiragana \"な\" represent?","Select the correct romanization.","Ma","Ra","Na","Sa","2"},
            {"Speaking","100","How do you politely say \"Please\" when making a request?","Choose the most appropriate word.","Douzo","Onegaishimasu","Arigatou","Hai","1"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_screen);
        db     = DatabaseHelper.getInstance(this);
        userId = SessionManager.getUserId(this);
        initViews();
        setupClickListeners();
        for (int i = 0; i < 10; i++) userAnswers[i] = -1;
        loadQuestion(currentQuestion);
        startTimer(secondsRemaining);
    }

    private void initViews() {
        btnBack           = findViewById(R.id.btnBack);
        btnReplay         = findViewById(R.id.btnReplay);
        btnPlay           = findViewById(R.id.btnPlay);
        optionA           = findViewById(R.id.optionA);
        optionB           = findViewById(R.id.optionB);
        optionC           = findViewById(R.id.optionC);
        optionD           = findViewById(R.id.optionD);
        btnSkip           = findViewById(R.id.btnSkip);
        btnCheckAnswer    = findViewById(R.id.btnCheckAnswer);
        tvCheckAnswer     = findViewById(R.id.tvCheckAnswer);
        tvTimer           = findViewById(R.id.tvTimer);
        tvQuestionCounter = findViewById(R.id.tvQuestionCounter);
        tvScore           = findViewById(R.id.tvScore);
        tvQuestionType    = findViewById(R.id.tvQuestionType);
        tvQuestionPoints  = findViewById(R.id.tvQuestionPoints);
        tvOptionA         = findViewById(R.id.tvOptionA);
        tvOptionB         = findViewById(R.id.tvOptionB);
        tvOptionC         = findViewById(R.id.tvOptionC);
        tvOptionD         = findViewById(R.id.tvOptionD);
        tvQuestionText    = findViewById(R.id.tvQuestionText);
        tvQuestionSub     = findViewById(R.id.tvQuestionSub);
        progressBar       = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnPlay.setOnClickListener(v ->
                Toast.makeText(this,"Playing audio…",Toast.LENGTH_SHORT).show());
        btnReplay.setOnClickListener(v ->
                Toast.makeText(this,"Replaying audio…",Toast.LENGTH_SHORT).show());
        optionA.setOnClickListener(v -> selectOption(0));
        optionB.setOnClickListener(v -> selectOption(1));
        optionC.setOnClickListener(v -> selectOption(2));
        optionD.setOnClickListener(v -> selectOption(3));
        btnSkip.setOnClickListener(v -> advanceQuestion(true));
        btnCheckAnswer.setOnClickListener(v -> {
            if (selectedOption == -1) {
                Toast.makeText(this,"Please select an answer first",Toast.LENGTH_SHORT).show();
                return;
            }
            recordAndFinish();
        });
        findViewById(R.id.tvReportIssue).setOnClickListener(v ->
                Toast.makeText(this,"Issue reported. Thank you!",Toast.LENGTH_SHORT).show());
    }

    private void loadQuestion(int index) {
        selectedOption = -1;
        String[] q = QUESTIONS[index];
        tvQuestionCounter.setText("Question " + (index+1) + " of " + QUESTIONS.length);
        tvScore.setText("Score: " + score + " pts");
        progressBar.setProgress((int)((index/(float)QUESTIONS.length)*100));
        tvQuestionType.setText(q[0]);
        tvQuestionPoints.setText("Point: " + q[1]);
        tvQuestionText.setText(q[2]);
        tvQuestionSub.setText(q[3]);
        tvOptionA.setText(q[4]); tvOptionB.setText(q[5]);
        tvOptionC.setText(q[6]); tvOptionD.setText(q[7]);
        boolean isListening = q[0].equals("Listening");
        findViewById(R.id.audioPlayerRow).setVisibility(isListening ? View.VISIBLE : View.GONE);
        resetOptions();
        setCheckAnswerEnabled(false);
    }

    private void selectOption(int option) {
        selectedOption = option;
        resetOptions();
        switch(option){
            case 0: optionA.setBackgroundResource(R.drawable.option_selected_bg); break;
            case 1: optionB.setBackgroundResource(R.drawable.option_selected_bg); break;
            case 2: optionC.setBackgroundResource(R.drawable.option_selected_bg); break;
            case 3: optionD.setBackgroundResource(R.drawable.option_selected_bg); break;
        }
        userAnswers[currentQuestion] = option;
        int correct = Integer.parseInt(QUESTIONS[currentQuestion][8]);
        if (option == correct) score += Integer.parseInt(QUESTIONS[currentQuestion][1]);
        if (currentQuestion == QUESTIONS.length - 1) {
            setCheckAnswerEnabled(true);
        } else {
            optionA.postDelayed(() -> advanceQuestion(false), 600);
        }
    }

    private void advanceQuestion(boolean skipped) {
        if (currentQuestion < QUESTIONS.length - 1) {
            currentQuestion++;
            loadQuestion(currentQuestion);
        }
    }

    private void recordAndFinish() {
        if (countDownTimer != null) countDownTimer.cancel();
        int timeTaken = 299 - secondsRemaining;

        int correctCount = 0;
        for (int i = 0; i < QUESTIONS.length; i++) {
            if (userAnswers[i] == Integer.parseInt(QUESTIONS[i][8])) correctCount++;
        }

        int masteryPct = (int) Math.round((correctCount / (double) QUESTIONS.length) * 100);
        String grade = masteryPct >= 90 ? "A+" : masteryPct >= 85 ? "A" : masteryPct >= 80 ? "A-"
                : masteryPct >= 75 ? "B+" : masteryPct >= 70 ? "B" : masteryPct >= 65 ? "B-" : "C";

        // ── Save to DB ──────────────────────────────────────────────────
        long testId = db.saveTestResult(userId, score, QUESTIONS.length,
                grade, masteryPct, timeTaken);

        for (int i = 0; i < QUESTIONS.length; i++) {
            int correctIdx = Integer.parseInt(QUESTIONS[i][8]);
            String[] opts = {QUESTIONS[i][4],QUESTIONS[i][5],QUESTIONS[i][6],QUESTIONS[i][7]};
            String userAns = userAnswers[i] >= 0 ? opts[userAnswers[i]] : "(Skipped)";
            db.saveQuestionResult(testId, i+1, QUESTIONS[i][0],
                    userAnswers[i] == correctIdx, userAns, opts[correctIdx]);
        }

        // Award XP based on score
        db.addXp(userId, score / 10);

        // ── Pass data to result screen ──────────────────────────────────
        Intent intent = new Intent(this, Testresultactivity.class);
        intent.putExtra("test_id",  testId);
        intent.putExtra("score",    score);
        intent.putExtra("correct",  correctCount);
        intent.putExtra("total",    QUESTIONS.length);
        intent.putExtra("userAnswers", userAnswers);
        String[] flat = new String[QUESTIONS.length * QUESTIONS[0].length];
        for (int i = 0; i < QUESTIONS.length; i++)
            for (int j = 0; j < QUESTIONS[i].length; j++)
                flat[i * QUESTIONS[i].length + j] = QUESTIONS[i][j];
        intent.putExtra("questions", flat);
        startActivity(intent);
        finish();
    }

    private void resetOptions() {
        optionA.setBackgroundResource(R.drawable.option_default_bg);
        optionB.setBackgroundResource(R.drawable.option_default_bg);
        optionC.setBackgroundResource(R.drawable.option_default_bg);
        optionD.setBackgroundResource(R.drawable.option_default_bg);
    }

    private void setCheckAnswerEnabled(boolean enabled) {
        if (enabled) {
            btnCheckAnswer.setBackgroundResource(R.drawable.btn_primary);
            tvCheckAnswer.setTextColor(0xFFFFFFFF);
        } else {
            btnCheckAnswer.setBackgroundResource(R.drawable.btn_check_disabled);
            tvCheckAnswer.setTextColor(0xFFAAAAAA);
        }
    }

    private void startTimer(int totalSeconds) {
        secondsRemaining = totalSeconds;
        countDownTimer = new CountDownTimer(totalSeconds * 1000L, 1000) {
            @Override public void onTick(long ms) {
                secondsRemaining = (int)(ms / 1000);
                tvTimer.setText(String.format("%02d:%02d", secondsRemaining/60, secondsRemaining%60));
            }
            @Override public void onFinish() {
                tvTimer.setText("00:00");
                Toast.makeText(TestScreenActivity.this,"Time's up!",Toast.LENGTH_SHORT).show();
                recordAndFinish();
            }
        }.start();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
