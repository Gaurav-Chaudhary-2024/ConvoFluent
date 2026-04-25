package com.example.mypage.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.mypage.R
import com.example.mypage.adapter.HiraganaAdapter
import com.example.mypage.model.AppData
import com.example.mypage.model.HiraganaChar

class ScriptFeedActivity : AppCompatActivity() {

    private lateinit var recyclerHiragana: RecyclerView
    private lateinit var btnContinue: Button
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var pillVowels: TextView
    private lateinit var pillKColumn: TextView
    private lateinit var pillSColumn: TextView
    private lateinit var pillT: TextView

    private val allPills get() = listOf(pillVowels, pillKColumn, pillSColumn, pillT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_script_feed)

        initViews()
        setupHiraganaGrid(AppData.hiraganaVowels)
        setupSectionPills()
        setupContinueButton()
        setupBottomNav()
    }

    private fun initViews() {
        recyclerHiragana = findViewById(R.id.recyclerHiragana)
        btnContinue      = findViewById(R.id.btnContinue)
        bottomNav        = findViewById(R.id.bottomNav)
        pillVowels       = findViewById(R.id.pillVowels)
        pillKColumn      = findViewById(R.id.pillKColumn)
        pillSColumn      = findViewById(R.id.pillSColumn)
        pillT            = findViewById(R.id.pillT)
    }

    private fun setupHiraganaGrid(chars: List<HiraganaChar>) {
        recyclerHiragana.layoutManager = GridLayoutManager(this, 2)
        recyclerHiragana.adapter = HiraganaAdapter(chars) { char, index ->
            ScriptLessonActivity.start(this, index)
        }
    }

    private fun setupSectionPills() {
        pillVowels.setOnClickListener  { activatePill(pillVowels);  setupHiraganaGrid(AppData.hiraganaVowels) }
        pillKColumn.setOnClickListener { activatePill(pillKColumn); setupHiraganaGrid(getKColumnChars()) }
        pillSColumn.setOnClickListener { activatePill(pillSColumn); setupHiraganaGrid(getSColumnChars()) }
        pillT.setOnClickListener       { activatePill(pillT);       setupHiraganaGrid(getTColumnChars()) }
    }

    private fun activatePill(active: TextView) {
        allPills.forEach { pill ->
            if (pill == active) {
                pill.setBackgroundResource(R.drawable.bg_pill_active)
                pill.setTextColor(getColor(R.color.text_primary))
            } else {
                pill.setBackgroundResource(R.drawable.bg_pill_inactive)
                pill.setTextColor(getColor(R.color.text_muted))
            }
        }
    }

    private fun setupContinueButton() {
        btnContinue.setOnClickListener {
            ScriptLessonActivity.start(this, startIndex = 2)
        }
    }

    private fun setupBottomNav() {
        bottomNav.selectedItemId = R.id.nav_script

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home   -> true
                R.id.nav_script -> true
                R.id.nav_lesson -> { LessonPlayerActivity.start(this); true }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun getKColumnChars() = listOf(
        HiraganaChar("か","KA","Someone doing karate","Sounds like 'ka' as in car","KArate kick!","かわ","KAWA","River"),
        HiraganaChar("き","KI","Like a key on a keychain","Sounds like 'ki' as in key","A KEY — 'KI'!","きつね","KITSUNE","Fox"),
        HiraganaChar("く","KU","Bird beak open wide","Sounds like 'ku' as in cool","A bird saying KU!","くも","KUMO","Cloud"),
        HiraganaChar("け","KE","Looks like a gate","Sounds like 'ke' as in kettle","A GATE — 'KE'!","けむり","KEMURI","Smoke"),
        HiraganaChar("こ","KO","Two horizontal lines","Sounds like 'ko' as in coat","Two stripes — 'KO'!","こころ","KOKORO","Heart"),
        HiraganaChar("さ","SA","Cross with a curly tail","Sounds like 'sa' as in sad","A person bowing — 'SA'!","さかな","SAKANA","Fish"),
    )

    private fun getSColumnChars() = listOf(
        HiraganaChar("さ","SA","Cross with a curly tail","Sounds like 'sa' as in sad","A person bowing!","さかな","SAKANA","Fish"),
        HiraganaChar("し","SHI","Looks like a fishhook","Sounds like 'shi' as in she","A fishing hook — 'SHI'!","しろ","SHIRO","White"),
        HiraganaChar("す","SU","Swan on water","Sounds like 'su' as in sue","A swan swimming — 'SU'!","すし","SUSHI","Sushi"),
        HiraganaChar("せ","SE","Like a hand waving","Sounds like 'se' as in set","A waving hand — 'SE'!","せかい","SEKAI","World"),
    )

    private fun getTColumnChars() = listOf(
        HiraganaChar("た","TA","Looks like a cross","Sounds like 'ta' as in tall","A cross — 'TA'!","たまご","TAMAGO","Egg"),
        HiraganaChar("ち","CHI","A person running","Sounds like 'chi' as in cheese","CHeeseburger runner!","ちず","CHIZU","Map"),
        HiraganaChar("つ","TSU","A smiley curve","Sounds like 'tsu' as in tsunami","A happy smile — 'TSU'!","つき","TSUKI","Moon"),
    )

    companion object {
        fun start(from: android.content.Context) {
            from.startActivity(Intent(from, ScriptFeedActivity::class.java))
        }
    }
}
