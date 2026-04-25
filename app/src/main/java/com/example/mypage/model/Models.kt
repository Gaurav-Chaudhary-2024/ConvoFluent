package com.example.mypage.model

// ── Chat message for LessonPlayer ──
data class ChatMessage(
    val text: String,
    val sender: Sender,
    val time: String
) {
    enum class Sender { AI, USER }
}

// ── Hiragana character model ──
data class HiraganaChar(
    val character: String,
    val romaji: String,
    val hint: String,
    val soundDescription: String,
    val memoryHack: String,
    val exampleWord: String,
    val exampleRomaji: String,
    val exampleMeaning: String
)

// ── Lesson section model ──
data class LessonSection(
    val id: String,
    val title: String,
    val characters: List<HiraganaChar>,
    val level: Int = 1
)

// ── App-wide data source (singleton) ──
object AppData {

    val hiraganaVowels = listOf(
        HiraganaChar(
            character = "あ",
            romaji = "A",
            hint = "Looks like a cross and noodle",
            soundDescription = "Sounds like 'a' as in Father",
            memoryHack = "It looks like a big 'A' inside a circle!",
            exampleWord = "あき",
            exampleRomaji = "AKI",
            exampleMeaning = "Autumn"
        ),
        HiraganaChar(
            character = "い",
            romaji = "I",
            hint = "Two lines that look like eels",
            soundDescription = "Sounds like 'ee' as in eat",
            memoryHack = "Two people standing — 'I' and another 'I'!",
            exampleWord = "いぬ",
            exampleRomaji = "INU",
            exampleMeaning = "Dog"
        ),
        HiraganaChar(
            character = "う",
            romaji = "U",
            hint = "Arrow turned sideways",
            soundDescription = "Sounds like 'oo' as in moon",
            memoryHack = "Looks like a mouth saying 'U'!",
            exampleWord = "うみ",
            exampleRomaji = "UMI",
            exampleMeaning = "Sea"
        ),
        HiraganaChar(
            character = "え",
            romaji = "E",
            hint = "Looks like an exotic bird",
            soundDescription = "Sounds like 'e' as in bed",
            memoryHack = "An acrobat doing a backbend — 'E'xtraordinary!",
            exampleWord = "えき",
            exampleRomaji = "EKI",
            exampleMeaning = "Station"
        ),
        HiraganaChar(
            character = "お",
            romaji = "O",
            hint = "Looks like a person on a golf tee",
            soundDescription = "Sounds like 'o' as in oh",
            memoryHack = "Person swinging a golf club — 'O'!",
            exampleWord = "おと",
            exampleRomaji = "OTO",
            exampleMeaning = "Sound"
        ),
        HiraganaChar(
            character = "か",
            romaji = "KA",
            hint = "Someone doing karate",
            soundDescription = "Sounds like 'ka' as in car",
            memoryHack = "KArate kick — 'KA'!",
            exampleWord = "かわ",
            exampleRomaji = "KAWA",
            exampleMeaning = "River"
        )
    )

    val sampleChatMessages = listOf(
        ChatMessage(
            text = "Irasshaimase! Welcome to our Izakaya. Are you ready to order your first drink?",
            sender = ChatMessage.Sender.AI,
            time = "18:42"
        ),
        ChatMessage(
            text = "Sumimasen, biiru o hitotsu kudasai!",
            sender = ChatMessage.Sender.USER,
            time = "18:43"
        ),
        ChatMessage(
            text = "Excellent! 'Hitotsu' is the correct counter for general objects.",
            sender = ChatMessage.Sender.AI,
            time = "18:43"
        ),
        ChatMessage(
            text = "Hai, kashikomari mashita! Would you like some edamame or gyoza to start with?",
            sender = ChatMessage.Sender.AI,
            time = "18:44"
        )
    )
}