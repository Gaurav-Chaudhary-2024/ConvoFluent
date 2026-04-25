package com.example.mypage.ui

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypage.R
import com.example.mypage.adapter.ChatAdapter
import com.example.mypage.model.AppData
import com.example.mypage.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LessonPlayerActivity : AppCompatActivity() {

    private lateinit var recyclerChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnBack: ImageButton

    private val chatAdapter = ChatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_player)

        initViews()
        setupRecycler()
        loadInitialMessages()
        setupListeners()
    }

    private fun initViews() {
        recyclerChat = findViewById(R.id.recyclerChat)
        etMessage    = findViewById(R.id.etMessage)
        btnSend      = findViewById(R.id.btnSend)
        btnBack      = findViewById(R.id.btnBack)
    }

    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerChat.layoutManager = layoutManager
        recyclerChat.adapter        = chatAdapter
    }

    private fun loadInitialMessages() {
        chatAdapter.setMessages(AppData.sampleChatMessages)
        scrollToBottom()
    }

    private fun setupListeners() {
        btnSend.setOnClickListener { sendUserMessage() }

        etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendUserMessage()
                true
            } else false
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun sendUserMessage() {
        val text = etMessage.text.toString().trim()
        if (text.isEmpty()) return

        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val userMsg = ChatMessage(text, ChatMessage.Sender.USER, time)
        chatAdapter.addMessage(userMsg)
        etMessage.setText("")
        scrollToBottom()

        recyclerChat.postDelayed({
            val aiReply = generateAiReply(text)
            chatAdapter.addMessage(ChatMessage(aiReply, ChatMessage.Sender.AI, time))
            scrollToBottom()
        }, 800)
    }

    private fun generateAiReply(userInput: String): String {
        return when {
            userInput.contains("biiru", ignoreCase = true) ||
            userInput.contains("beer",  ignoreCase = true)  ->
                "Kanpai! 🍺 'Biiru' (ビール) means beer. Would you like to practice ordering food next?"

            userInput.contains("kudasai", ignoreCase = true) ->
                "Great use of 'kudasai'! It's a polite way to say 'please give me'. Try asking for gyoza next!"

            userInput.contains("sumimasen", ignoreCase = true) ->
                "'Sumimasen' is perfect for getting attention politely. Well done!"

            else ->
                "Ii desu ne! (Good!) Keep practicing. Try ordering something using 'hitotsu kudasai'."
        }
    }

    private fun scrollToBottom() {
        recyclerChat.post {
            recyclerChat.scrollToPosition(chatAdapter.itemCount - 1)
        }
    }

    companion object {
        fun start(from: android.content.Context) {
            from.startActivity(Intent(from, LessonPlayerActivity::class.java))
        }
    }
}
