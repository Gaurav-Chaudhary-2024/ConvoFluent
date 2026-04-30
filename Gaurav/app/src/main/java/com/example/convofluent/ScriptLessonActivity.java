package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ScriptLessonActivity extends AppCompatActivity {

    private static final String COLUMN_NAME = "Vowels";

    private ImageButton btnBack, btnReference, btnPlayUsage;
    private Button      btnListen, btnTraceChar, btnNextChar;
    private TextView    tvMainCharacter, tvSoundsLike, tvMemoryTip;
    private TextView    tvExampleWord, tvExampleRomanization, tvExampleMeaning;
    private TextView    tvSetProgress;
    private ProgressBar progressBarSet;
    private LinearLayout[] stripItems  = new LinearLayout[5];
    private TextView[]    stripChars  = new TextView[5];
    private TextView[]    stripRomaji = new TextView[5];

    private DatabaseHelper db;
    private long userId;
    private int  currentIndex = 0;

    private static class CharacterData {
        String japanese,romanization,soundsLike,memoryTip,exampleWord,exampleRomanization,exampleMeaning;
        CharacterData(String j,String r,String s,String m,String ew,String er,String em){
            japanese=j;romanization=r;soundsLike=s;memoryTip=m;
            exampleWord=ew;exampleRomanization=er;exampleMeaning=em;
        }
    }

    private final CharacterData[] characters = {
            new CharacterData("あ","A","\"a\" as in Father","Looks like a big 'A' inside a circle!","あき","AKI","Autumn"),
            new CharacterData("い","I","\"i\" as in bIt","Two people standing side by side — 'Eee'!","いぬ","INU","Dog"),
            new CharacterData("う","U","\"u\" as in tUne","A bowl facing up — 'Ooo'.","うみ","UMI","Sea"),
            new CharacterData("え","E","\"e\" as in bEd","Stick figure with arms out — 'Eh?'","えき","EKI","Station"),
            new CharacterData("お","O","\"o\" as in mOre","Person bowing respectfully — 'Oh!'","おに","ONI","Demon"),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_lesson);
        db     = DatabaseHelper.getInstance(this);
        userId = SessionManager.getUserId(this);
        initViews();
        setupClickListeners();

        // Resume from DB (or Intent from AlphabetActivity)
        DatabaseHelper.AlphabetProgress saved = db.getAlphabetProgress(userId, COLUMN_NAME);
        currentIndex = getIntent().getIntExtra("start_index", saved.charIndex);
        updateCharacterUI();
    }

    private void initViews() {
        btnBack            = findViewById(R.id.btnBack);
        btnReference       = findViewById(R.id.btnReference);
        btnPlayUsage       = findViewById(R.id.btnPlayUsage);
        btnListen          = findViewById(R.id.btnListen);
        btnTraceChar       = findViewById(R.id.btnTraceChar);
        btnNextChar        = findViewById(R.id.btnNextChar);
        tvMainCharacter    = findViewById(R.id.tvMainCharacter);
        tvSoundsLike       = findViewById(R.id.tvSoundsLike);
        tvMemoryTip        = findViewById(R.id.tvMemoryTip);
        tvExampleWord      = findViewById(R.id.tvExampleWord);
        tvExampleRomanization = findViewById(R.id.tvExampleRomanization);
        tvExampleMeaning   = findViewById(R.id.tvExampleMeaning);
        tvSetProgress      = findViewById(R.id.tvSetProgress);
        progressBarSet     = findViewById(R.id.progressBarSet);
        int[] itemIds  = {R.id.stripItem1,R.id.stripItem2,R.id.stripItem3,R.id.stripItem4,R.id.stripItem5};
        int[] charIds  = {R.id.stripChar1,R.id.stripChar2,R.id.stripChar3,R.id.stripChar4,R.id.stripChar5};
        int[] romaIds  = {R.id.stripRomaji1,R.id.stripRomaji2,R.id.stripRomaji3,R.id.stripRomaji4,R.id.stripRomaji5};
        for (int i=0;i<5;i++) {
            stripItems[i]  = findViewById(itemIds[i]);
            stripChars[i]  = findViewById(charIds[i]);
            stripRomaji[i] = findViewById(romaIds[i]);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnReference.setOnClickListener(v ->
                Toast.makeText(this,"Reference guide coming soon!",Toast.LENGTH_SHORT).show());
        btnListen.setOnClickListener(v ->
                Toast.makeText(this,"Playing: "+characters[currentIndex].japanese
                        +" sounds like "+characters[currentIndex].soundsLike,Toast.LENGTH_SHORT).show());
        btnPlayUsage.setOnClickListener(v ->
                Toast.makeText(this,"Playing: "+characters[currentIndex].exampleWord
                        +" ("+characters[currentIndex].exampleRomanization+") - "
                        +characters[currentIndex].exampleMeaning,Toast.LENGTH_SHORT).show());
        btnTraceChar.setOnClickListener(v ->
                startActivity(new Intent(this, TestScreenActivity.class)));

        btnNextChar.setOnClickListener(v -> {
            if (currentIndex < characters.length - 1) {
                currentIndex++;
                // Save progress to DB
                db.saveAlphabetProgress(userId, COLUMN_NAME, currentIndex, currentIndex);
                db.addXp(userId, 5); // 5 XP per character
                updateCharacterUI();
            } else {
                // Section complete — reset to 0
                db.saveAlphabetProgress(userId, COLUMN_NAME, 0, characters.length);
                Toast.makeText(this,"Section complete! 🎉",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AlphabetActivity.class));
                finish();
            }
        });
    }

    private void updateCharacterUI() {
        CharacterData c = characters[currentIndex];
        tvMainCharacter.setText(c.japanese);
        tvSoundsLike.setText("Sounds like: " + c.soundsLike);
        tvMemoryTip.setText(c.memoryTip);
        tvExampleWord.setText(c.exampleWord);
        tvExampleRomanization.setText(c.exampleRomanization);
        tvExampleMeaning.setText(c.exampleMeaning);
        tvSetProgress.setText((currentIndex + 1) + " / 5 MASTERED");
        progressBarSet.setProgress((currentIndex + 1) * 20);
        btnListen.setText("  Listen: " + c.japanese);
        btnNextChar.setText(currentIndex == characters.length - 1 ? "Complete! ✓" : "Next Character →");
        updateCharacterStrip();
    }

    private void updateCharacterStrip() {
        for (int i = 0; i < stripItems.length; i++) {
            if (stripItems[i] == null) continue;
            if (i == currentIndex) {
                stripItems[i].setBackgroundResource(R.drawable.char_strip_active);
                if(stripChars[i]!=null)  stripChars[i].setTextColor(0xFF6B3EFF);
                if(stripRomaji[i]!=null) stripRomaji[i].setTextColor(0xFF6B3EFF);
                stripItems[i].setAlpha(1f);
            } else if (i < currentIndex) {
                stripItems[i].setBackgroundResource(R.drawable.char_strip_inactive);
                if(stripChars[i]!=null)  stripChars[i].setTextColor(0xFF00BFA5);
                if(stripRomaji[i]!=null) stripRomaji[i].setTextColor(0xFF00BFA5);
                stripItems[i].setAlpha(0.85f);
            } else {
                stripItems[i].setBackgroundResource(R.drawable.char_strip_inactive);
                if(stripChars[i]!=null)  stripChars[i].setTextColor(0xFF1A1A2E);
                if(stripRomaji[i]!=null) stripRomaji[i].setTextColor(0xFF888888);
                stripItems[i].setAlpha(0.4f);
            }
        }
    }
}