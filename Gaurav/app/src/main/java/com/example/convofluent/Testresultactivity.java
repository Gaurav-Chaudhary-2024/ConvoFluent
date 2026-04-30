package com.example.convofluent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class Testresultactivity extends AppCompatActivity {

    private static final int Q_FIELDS = 9;

    private DatabaseHelper db;
    private long userId;
    private long testId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        db     = DatabaseHelper.getInstance(this);
        userId = SessionManager.getUserId(this);
        testId = getIntent().getLongExtra("test_id", -1);

        // ── Get data — prefer DB, fall back to Intent ──────────────────
        int score       = getIntent().getIntExtra("score",   0);
        int correct     = getIntent().getIntExtra("correct", 0);
        int total       = getIntent().getIntExtra("total",   10);
        int[]    userAnswers = getIntent().getIntArrayExtra("userAnswers");
        String[] flat        = getIntent().getStringArrayExtra("questions");

        // If test was saved to DB, reload question results from there
        List<DatabaseHelper.QuestionResult> dbResults = null;
        if (testId != -1) {
            dbResults = db.getQuestionResults(testId);
        }

        // ── Mastery + Grade ────────────────────────────────────────────
        int masteryPct = (int) Math.round((correct / (double) total) * 100);
        String grade   = gradeFor(masteryPct);

        // ── Bind header ────────────────────────────────────────────────
        setText(R.id.tvMasteryPct,      masteryPct + "%");
        setText(R.id.tvXpEarned,        "+" + score);
        setText(R.id.tvGrade,           grade);

        // Personalise with real username
        DatabaseHelper.User user = db.getUser(userId);
        String firstName = user != null
                ? (user.fullName.contains(" ")
                ? user.fullName.substring(0, user.fullName.indexOf(" "))
                : user.fullName)
                : "You";
        setText(R.id.tvMasterySubtitle,
                "Great job, " + firstName + "! Keep going to reach B2 fluency.");

        // ── Skill breakdown ────────────────────────────────────────────
        int[] totals   = new int[5]; // Listening, Speaking, Reading, Grammar, Vocabulary
        int[] corrects = new int[5];

        if (dbResults != null && !dbResults.isEmpty()) {
            for (DatabaseHelper.QuestionResult qr : dbResults) {
                int idx = categoryIndex(qr.category);
                if (idx >= 0) { totals[idx]++; if (qr.wasCorrect) corrects[idx]++; }
            }
        } else if (flat != null && userAnswers != null) {
            for (int i = 0; i < total; i++) {
                String type    = flat[i * Q_FIELDS];
                int correctIdx = Integer.parseInt(flat[i * Q_FIELDS + 8]);
                boolean right  = userAnswers[i] == correctIdx;
                int idx        = categoryIndex(type);
                if (idx >= 0) { totals[idx]++; if (right) corrects[idx]++; }
            }
        }

        setBreakdownPct(R.id.tvListeningPct, totals[0], corrects[0]);
        setBreakdownPct(R.id.tvSpeakingPct,  totals[1], corrects[1]);
        setBreakdownPct(R.id.tvReadingPct,   totals[2], corrects[2]);
        setBreakdownPct(R.id.tvWritingPct,   totals[3], corrects[3]);

        // ── Detailed Analysis ──────────────────────────────────────────
        LinearLayout detailContainer = findViewById(R.id.detailContainer);
        setText(R.id.tvShowingCount, "Showing " + total + " Questions");

        if (detailContainer != null) {
            detailContainer.removeAllViews();

            if (dbResults != null && !dbResults.isEmpty()) {
                // Build from DB results
                for (DatabaseHelper.QuestionResult qr : dbResults) {
                    detailContainer.addView(buildQuestionCard(
                            qr.questionNum, qr.category,
                            getFlatPrompt(flat, qr.questionNum - 1),
                            qr.userAnswer, qr.correctAnswer, qr.wasCorrect));
                }
            } else if (flat != null && userAnswers != null) {
                // Build from Intent data
                for (int i = 0; i < total; i++) {
                    String type      = flat[i * Q_FIELDS];
                    String prompt    = flat[i * Q_FIELDS + 2];
                    String[] opts    = {flat[i*Q_FIELDS+4],flat[i*Q_FIELDS+5],flat[i*Q_FIELDS+6],flat[i*Q_FIELDS+7]};
                    int correctIdx   = Integer.parseInt(flat[i * Q_FIELDS + 8]);
                    int userIdx      = userAnswers[i];
                    boolean isRight  = userIdx == correctIdx;
                    String userAns   = userIdx >= 0 ? opts[userIdx] : "(Skipped)";
                    detailContainer.addView(buildQuestionCard(
                            i + 1, type, prompt, userAns, opts[correctIdx], isRight));
                }
            }
        }

        // ── Lifetime stats from DB ─────────────────────────────────────
        int testCount = db.getTestCount(userId);
        setText(R.id.tvTestCount, testCount + " tests taken");

        // ── Bottom buttons ─────────────────────────────────────────────
        View btnShare = findViewById(R.id.btnShare);
        if (btnShare != null) btnShare.setOnClickListener(v ->
                Toast.makeText(this, "Sharing result…", Toast.LENGTH_SHORT).show());

        View btnRetake = findViewById(R.id.btnRetake);
        if (btnRetake != null) btnRetake.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, TestScreenActivity.class));
        });

        View btnReviewMistakes = findViewById(R.id.btnReviewMistakes);
        if (btnReviewMistakes != null) btnReviewMistakes.setOnClickListener(v ->
                Toast.makeText(this, "Review mode coming soon!", Toast.LENGTH_SHORT).show());

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    // ── Card builder ───────────────────────────────────────────────────────
    private View buildQuestionCard(int num, String type, String prompt,
                                   String userAns, String correctAns, boolean isRight) {
        View card = getLayoutInflater().inflate(
                R.layout.item_result_question, null, false);

        setText(card, R.id.tvQNum,    "Question " + num);
        setText(card, R.id.tvQType,   type);
        setText(card, R.id.tvQPrompt, prompt != null ? prompt : "");
        setText(card, R.id.tvUserAns, userAns);

        TextView tvStatus = card.findViewById(R.id.tvQStatus);
        if (tvStatus != null) {
            tvStatus.setText(isRight ? "✓" : "✗");
            tvStatus.setTextColor(isRight ? 0xFF4CAF50 : 0xFFE53935);
        }

        TextView tvUserAnsView = card.findViewById(R.id.tvUserAns);
        LinearLayout correctRow   = card.findViewById(R.id.correctAnswerRow);
        TextView tvCorrectAns     = card.findViewById(R.id.tvCorrectAns);
        LinearLayout expertTipRow = card.findViewById(R.id.expertTipRow);
        TextView tvExpertTip      = card.findViewById(R.id.tvExpertTip);

        if (isRight) {
            if (tvUserAnsView != null) {
                tvUserAnsView.setTextColor(0xFF1A1A2E);
                tvUserAnsView.setBackgroundResource(R.drawable.result_answer_correct_bg);
            }
            if (correctRow   != null) correctRow.setVisibility(View.GONE);
            if (expertTipRow != null) expertTipRow.setVisibility(View.GONE);
        } else {
            if (tvUserAnsView != null) {
                tvUserAnsView.setTextColor(0xFFE53935);
                tvUserAnsView.setBackgroundResource(R.drawable.result_answer_wrong_bg);
            }
            if (correctRow != null) {
                correctRow.setVisibility(View.VISIBLE);
                if (tvCorrectAns != null) tvCorrectAns.setText(correctAns);
            }
            if (expertTipRow != null) {
                expertTipRow.setVisibility(View.VISIBLE);
                if (tvExpertTip != null) tvExpertTip.setText(expertTip(type));
            }
        }
        return card;
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private String getFlatPrompt(String[] flat, int questionIndex) {
        if (flat == null || questionIndex < 0) return "";
        int base = questionIndex * Q_FIELDS;
        return base + 2 < flat.length ? flat[base + 2] : "";
    }

    private int categoryIndex(String type) {
        switch (type) {
            case "Listening":  return 0;
            case "Speaking":   return 1;
            case "Reading":    return 2;
            case "Grammar":
            case "Writing":    return 3;
            case "Vocabulary": return 4;
            default:           return -1;
        }
    }

    private String gradeFor(int pct) {
        if (pct >= 90) return "A+";
        if (pct >= 85) return "A";
        if (pct >= 80) return "A-";
        if (pct >= 75) return "B+";
        if (pct >= 70) return "B";
        if (pct >= 65) return "B-";
        return "C";
    }

    private void setBreakdownPct(int viewId, int total, int correct) {
        TextView tv = findViewById(viewId);
        if (tv == null) return;
        int pct = total == 0 ? 0 : (int) Math.round((correct / (double) total) * 100);
        tv.setText(pct + "%");
    }

    private void setText(int viewId, String text) {
        TextView tv = findViewById(viewId);
        if (tv != null) tv.setText(text);
    }

    private void setText(View root, int viewId, String text) {
        TextView tv = root.findViewById(viewId);
        if (tv != null) tv.setText(text);
    }

    private String expertTip(String type) {
        switch (type) {
            case "Listening":  return "Listen to native Japanese conversations daily to improve comprehension.";
            case "Speaking":   return "Focus on mouth shape. Practice vowel sounds slowly before full words.";
            case "Grammar":    return "Review Japanese particle usage — they mark grammatical roles.";
            case "Reading":    return "Practice hiragana and katakana for 5 minutes daily to build speed.";
            case "Vocabulary": return "Use spaced-repetition flashcards to reinforce new vocabulary.";
            default:           return "Review this topic in your lesson materials with example sentences.";
        }
    }
}
