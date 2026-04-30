package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class AlphabetActivity extends AppCompatActivity {

    private static final String COLUMN_NAME = "Vowels"; // current lesson column

    private ImageButton btnSearch, btnHelp;
    private Button      btnContinue;
    private LinearLayout tabHome, tabLessons, tabAlphabet, tabProfile;
    private TextView    tabVowels, tabKColumn, tabSColumn, tabTColumn, tabNColumn;
    private TextView    tvSectionTitle, tvSectionSubtitle;
    private TextView    tvMasteredCount, tvRemainingCount;
    private ProgressBar progressFocus;
    private GridLayout  characterGrid;

    private DatabaseHelper db;
    private long userId;

    private static class CharData {
        String kana, romaji, tip; boolean mastered;
        CharData(String k,String r,String t,boolean m){kana=k;romaji=r;tip=t;mastered=m;}
    }

    private static final CharData[] VOWELS   = {
            new CharData("あ","A","Looks like a 'cross' and 'noodle'.",true),
            new CharData("い","I","Two lines like eels.",true),
            new CharData("う","U","Mouth saying 'ooh'.",false),
            new CharData("え","E","Looks like an exotic bird.",false),
            new CharData("お","O","Person on a golf tee.",false),
    };
    private static final CharData[] K_COLUMN = {
            new CharData("か","KA","Karate kick.",false),
            new CharData("き","KI","Key in a lock.",false),
            new CharData("く","KU","Bird's beak.",false),
            new CharData("け","KE","Keg on its side.",false),
            new CharData("こ","KO","Coat hanger.",false),
    };
    private static final CharData[] S_COLUMN = {
            new CharData("さ","SA","Samurai sword.",false),
            new CharData("し","SHI","Fishing hook.",false),
            new CharData("す","SU","Tornado swirl.",false),
            new CharData("せ","SE","Broken seat.",false),
            new CharData("そ","SO","Curvy Z.",false),
    };
    private static final CharData[] T_COLUMN = {
            new CharData("た","TA","Tai-chi pose.",false),
            new CharData("ち","CHI","Cheerleader.",false),
            new CharData("つ","TSU","Tsunami wave.",false),
            new CharData("て","TE","TV antenna.",false),
            new CharData("と","TO","Toe in a sock.",false),
    };
    private static final CharData[] N_COLUMN = {
            new CharData("な","NA","A knife.",false),
            new CharData("に","NI","Number 2 lines.",false),
            new CharData("ぬ","NU","Swirling noodles.",false),
            new CharData("ね","NE","Cat's tail.",false),
            new CharData("の","NO","Swirly no sign.",false),
    };
    private static final String[][] SECTION_META = {
            {"The Vowels","Fundamental sounds of the script"},
            {"The K-Column","K-sounds: KA, KI, KU, KE, KO"},
            {"The S-Column","S-sounds: SA, SHI, SU, SE, SO"},
            {"The T-Column","T-sounds: TA, CHI, TSU, TE, TO"},
            {"The N-Column","N-sounds: NA, NI, NU, NE, NO"},
    };

    private int activeTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alphabet);
        db     = DatabaseHelper.getInstance(this);
        userId = SessionManager.getUserId(this);
        initViews();
        setupClickListeners();
        selectTab(0);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                startActivity(new Intent(AlphabetActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFocusCard();
    }

    private void initViews() {
        btnSearch   = findViewById(R.id.btnSearch);
        btnHelp     = findViewById(R.id.btnHelp);
        btnContinue = findViewById(R.id.btnContinue);
        tabHome     = findViewById(R.id.tabHome);
        tabLessons  = findViewById(R.id.tabLessons);
        tabAlphabet = findViewById(R.id.tabAlphabet);
        tabProfile  = findViewById(R.id.tabProfile);
        tabVowels   = findViewById(R.id.tabVowels);
        tabKColumn  = findViewById(R.id.tabKColumn);
        tabSColumn  = findViewById(R.id.tabSColumn);
        tabTColumn  = findViewById(R.id.tabTColumn);
        tabNColumn  = findViewById(R.id.tabNColumn);
        tvSectionTitle    = findViewById(R.id.tvSectionTitle);
        tvSectionSubtitle = findViewById(R.id.tvSectionSubtitle);
        tvMasteredCount   = findViewById(R.id.tvMasteredCount);
        tvRemainingCount  = findViewById(R.id.tvRemainingCount);
        progressFocus     = findViewById(R.id.progressFocus);
        characterGrid     = findViewById(R.id.characterGrid);
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v ->
                Toast.makeText(this,"Search coming soon!",Toast.LENGTH_SHORT).show());
        btnHelp.setOnClickListener(v ->
                Toast.makeText(this,"Help coming soon!",Toast.LENGTH_SHORT).show());

        btnContinue.setOnClickListener(v -> {
            // Pass saved char index from DB
            DatabaseHelper.AlphabetProgress p = db.getAlphabetProgress(userId, COLUMN_NAME);
            Intent intent = new Intent(this, ScriptLessonActivity.class);
            intent.putExtra("start_index", p.charIndex);
            startActivity(intent);
        });

        tabVowels.setOnClickListener(v   -> selectTab(0));
        tabKColumn.setOnClickListener(v  -> selectTab(1));
        tabSColumn.setOnClickListener(v  -> selectTab(2));
        tabTColumn.setOnClickListener(v  -> selectTab(3));
        tabNColumn.setOnClickListener(v  -> selectTab(4));

        tabHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class)); finish();
        });
        tabLessons.setOnClickListener(v -> {
            String last = db.getLastOpenedLesson(userId);
            Class<?> cls = last == null ? DailyExpressionsCommuting.class :
                    last.equals("OrderingAtIzakaya") ? OrderingAtIzakaya.class :
                            last.equals("AnimeSlangCasualTalk") ? AnimeSlangCasualTalk.class :
                                    DailyExpressionsCommuting.class;
            startActivity(new Intent(this, cls));
        });
        tabAlphabet.setOnClickListener(v ->
                Toast.makeText(this,"You are on Alphabet",Toast.LENGTH_SHORT).show());
        tabProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class)); finish();
        });
    }

    private void selectTab(int index) {
        activeTabIndex = index;
        TextView[] pills = {tabVowels,tabKColumn,tabSColumn,tabTColumn,tabNColumn};
        for (int i = 0; i < pills.length; i++) {
            if (pills[i] == null) continue;
            pills[i].setBackgroundResource(i==index ? R.drawable.tab_active : R.drawable.tab_inactive);
            pills[i].setTextColor(i==index ? 0xFFFFFFFF : 0xFF444444);
        }
        tvSectionTitle.setText(SECTION_META[index][0]);
        tvSectionSubtitle.setText(SECTION_META[index][1]);
        buildGrid(getColumn(index));
    }

    private CharData[] getColumn(int i) {
        switch(i){case 1:return K_COLUMN;case 2:return S_COLUMN;case 3:return T_COLUMN;case 4:return N_COLUMN;default:return VOWELS;}
    }

    private void buildGrid(CharData[] data) {
        characterGrid.removeAllViews();
        for (CharData c : data) {
            LinearLayout card = new LinearLayout(this);
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f);
            p.width = 0; p.setMargins(dp(6),dp(6),dp(6),dp(6));
            card.setLayoutParams(p);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.char_card_background);
            card.setPadding(dp(12),dp(12),dp(12),dp(12));

            if (c.mastered) {
                ImageView check = new ImageView(this);
                LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(dp(18),dp(18));
                cp.gravity = Gravity.END; check.setLayoutParams(cp);
                check.setImageResource(R.drawable.ic_check_teal); card.addView(check);
            } else {
                View sp = new View(this);
                sp.setLayoutParams(new LinearLayout.LayoutParams(dp(18),dp(18))); card.addView(sp);
            }

            TextView tvK = new TextView(this);
            LinearLayout.LayoutParams kp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            kp.bottomMargin = dp(2); tvK.setLayoutParams(kp);
            tvK.setText(c.kana); tvK.setTextSize(44f);
            tvK.setGravity(Gravity.CENTER); tvK.setTextColor(0xFF1A1A2E); card.addView(tvK);

            TextView tvR = new TextView(this);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            rp.bottomMargin = dp(6); tvR.setLayoutParams(rp);
            tvR.setText(c.romaji); tvR.setTextSize(13f);
            tvR.setTypeface(null,android.graphics.Typeface.BOLD);
            tvR.setGravity(Gravity.CENTER); tvR.setTextColor(0xFF6B3EFF); card.addView(tvR);

            View div = new View(this);
            LinearLayout.LayoutParams dp2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,dp(1));
            dp2.bottomMargin = dp(6); div.setLayoutParams(dp2);
            div.setBackgroundColor(0xFFEEEEEE); card.addView(div);

            LinearLayout bot = new LinearLayout(this);
            bot.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            bot.setOrientation(LinearLayout.HORIZONTAL);
            bot.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvT = new TextView(this);
            tvT.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            tvT.setText(c.tip); tvT.setTextSize(10f); tvT.setTextColor(0xFFAAAAAA);
            tvT.setTypeface(null,android.graphics.Typeface.ITALIC); bot.addView(tvT);

            ImageView vol = new ImageView(this);
            vol.setLayoutParams(new LinearLayout.LayoutParams(dp(16),dp(16)));
            vol.setImageResource(R.drawable.ic_volume);
            final String label = c.kana + " (" + c.romaji + ")";
            vol.setOnClickListener(vv ->
                    Toast.makeText(this,"Playing: "+label,Toast.LENGTH_SHORT).show());
            bot.addView(vol); card.addView(bot);
            characterGrid.addView(card);
        }
    }

    private void updateFocusCard() {
        DatabaseHelper.AlphabetProgress p = db.getAlphabetProgress(userId, COLUMN_NAME);
        int mastered   = p.masteredCount;
        int remaining  = 5 - mastered;
        int pct        = mastered * 20;
        if (tvMasteredCount  != null) tvMasteredCount.setText(mastered + " Mastered");
        if (tvRemainingCount != null) tvRemainingCount.setText(remaining + " Remaining");
        if (progressFocus    != null) progressFocus.setProgress(pct);
        if (btnContinue      != null) btnContinue.setText(
                mastered == 0 ? "Start" : mastered == 5 ? "Review" : "Continue");
    }

    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}