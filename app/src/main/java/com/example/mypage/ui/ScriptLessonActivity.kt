package com.example.mypage.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mypage.R
import com.example.mypage.model.AppData
import com.example.mypage.model.HiraganaChar
import java.util.Locale

class ScriptLessonActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var btnBack: ImageButton
    private lateinit var progressLesson: ProgressBar
    private lateinit var tvProgressLabel: TextView
    private lateinit var tvMainChar: TextView
    private lateinit var tvSoundsLike: TextView
    private lateinit var tvMemoryHack: TextView
    private lateinit var tvExampleKanji: TextView
    private lateinit var tvExampleMeaning: TextView
    private lateinit var btnListen: Button
    private lateinit var btnExampleSound: ImageButton
    private lateinit var btnTraceChar: Button
    private lateinit var btnNextChar: Button

    private lateinit var tileA: LinearLayout
    private lateinit var tileI: LinearLayout
    private lateinit var tileU: LinearLayout
    private lateinit var tileE: LinearLayout

    private val chars = AppData.hiraganaVowels
    private var currentIndex = 0
    private val masteredSet = mutableSetOf(0, 1)

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_script_lesson)

        currentIndex = intent.getIntExtra(EXTRA_START_INDEX, 0)
            .coerceIn(0, chars.size - 1)

        initViews()
        setupTts()
        loadCharacter(currentIndex)
        setupListeners()
        updateCharStrip()
    }

    private fun initViews() {
        btnBack          = findViewById(R.id.btnBack)
        progressLesson   = findViewById(R.id.progressLesson)
        tvProgressLabel  = findViewById(R.id.tvProgressLabel)
        tvMainChar       = findViewById(R.id.tvMainChar)
        tvSoundsLike     = findViewById(R.id.tvSoundsLike)
        tvMemoryHack     = findViewById(R.id.tvMemoryHack)
        tvExampleKanji   = findViewById(R.id.tvExampleKanji)
        tvExampleMeaning = findViewById(R.id.tvExampleMeaning)
        btnListen        = findViewById(R.id.btnListen)
        btnExampleSound  = findViewById(R.id.btnExampleSound)
        btnTraceChar     = findViewById(R.id.btnTraceChar)
        btnNextChar      = findViewById(R.id.btnNextChar)
        tileA            = findViewById(R.id.tileA)
        tileI            = findViewById(R.id.tileI)
        tileU            = findViewById(R.id.tileU)
        tileE            = findViewById(R.id.tileE)
    }

    private fun setupTts() {
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.JAPANESE
            ttsReady = true
        }
    }

    private fun loadCharacter(index: Int) {
        val char = chars[index]

        tvMainChar.text       = char.character
        tvSoundsLike.text     = char.soundDescription
        tvMemoryHack.text     = char.memoryHack
        tvExampleKanji.text   = char.exampleWord
        tvExampleMeaning.text = "${char.exampleRomaji}\n${char.exampleMeaning}"

        val mastered = masteredSet.size
        val total    = chars.size
        val percent  = (mastered.toFloat() / total * 100).toInt()
        progressLesson.progress  = percent
        tvProgressLabel.text     = "$mastered / $total MASTERED"

        updateCharStrip()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnListen.setOnClickListener {
            speakJapanese(chars[currentIndex].character)
        }

        btnExampleSound.setOnClickListener {
            speakJapanese(chars[currentIndex].exampleWord)
        }

        btnTraceChar.setOnClickListener {
            showTraceDialog(chars[currentIndex])
        }

        btnNextChar.setOnClickListener {
            masteredSet.add(currentIndex)

            if (currentIndex < chars.size - 1) {
                currentIndex++
                loadCharacter(currentIndex)
                updateCharStrip()
            } else {
                ScriptFeedActivity.start(this)
                finish()
            }
        }
    }

    private fun updateCharStrip() {
        val tiles = listOf(tileA, tileI, tileU, tileE)
        tiles.forEachIndexed { index, tile ->
            val bg = when {
                index == currentIndex -> R.drawable.bg_char_selected
                masteredSet.contains(index) -> R.drawable.bg_char_selected
                else -> R.drawable.bg_char_card
            }
            tile.setBackgroundResource(bg)
        }
    }

    private fun speakJapanese(text: String) {
        if (ttsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun showTraceDialog(char: HiraganaChar) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Trace: ${char.character}")
            .setMessage("Tracing canvas would appear here.\nRomaji: ${char.romaji}")
            .setPositiveButton("Done") { d, _ -> d.dismiss() }
            .show()
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_START_INDEX = "start_index"

        fun start(from: Context, startIndex: Int = 0) {
            val intent = Intent(from, ScriptLessonActivity::class.java)
                .putExtra(EXTRA_START_INDEX, startIndex)
            from.startActivity(intent)
        }
    }
}
