package com.example.convofluent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DatabaseHelper — single source of truth for all SQLite operations.
 * One instance shared across the app via getInstance().
 *
 * Tables:
 *  1. users
 *  2. user_stats
 *  3. user_interests
 *  4. user_preferences
 *  5. lesson_progress
 *  6. alphabet_progress
 *  7. chat_history
 *  8. test_results
 *  9. test_question_results
 * 10. leaderboard
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // ── DB meta ────────────────────────────────────────────────────────────
    private static final String DB_NAME    = "convofluent.db";
    private static final int    DB_VERSION = 1;

    // ── Singleton ──────────────────────────────────────────────────────────
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TABLE CREATION
    // ══════════════════════════════════════════════════════════════════════

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. users
        db.execSQL("CREATE TABLE users (" +
                "id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                "full_name     TEXT NOT NULL," +
                "username      TEXT NOT NULL UNIQUE," +
                "email         TEXT NOT NULL UNIQUE," +
                "password_hash TEXT NOT NULL," +
                "gender        TEXT," +
                "language      TEXT," +
                "created_at    TEXT NOT NULL" +
                ")");

        // 2. user_stats
        db.execSQL("CREATE TABLE user_stats (" +
                "user_id        INTEGER PRIMARY KEY," +
                "streak         INTEGER DEFAULT 0," +
                "xp             INTEGER DEFAULT 0," +
                "level          INTEGER DEFAULT 1," +
                "rank           INTEGER DEFAULT 0," +
                "last_active    TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")");

        // 3. user_interests
        db.execSQL("CREATE TABLE user_interests (" +
                "id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id       INTEGER NOT NULL," +
                "interest_name TEXT NOT NULL," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")");

        // 4. user_preferences
        db.execSQL("CREATE TABLE user_preferences (" +
                "user_id         INTEGER PRIMARY KEY," +
                "daily_goal_mins INTEGER DEFAULT 15," +
                "difficulty      TEXT    DEFAULT 'Beginner'," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")");

        // 5. lesson_progress
        db.execSQL("CREATE TABLE lesson_progress (" +
                "id             INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id        INTEGER NOT NULL," +
                "lesson_name    TEXT    NOT NULL," +
                "progress_pct   INTEGER DEFAULT 0," +
                "last_opened_at TEXT," +
                "UNIQUE(user_id, lesson_name)," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")");

        // 6. alphabet_progress
        db.execSQL("CREATE TABLE alphabet_progress (" +
                "id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id       INTEGER NOT NULL," +
                "column_name   TEXT    NOT NULL," +
                "char_index    INTEGER DEFAULT 0," +
                "mastered_count INTEGER DEFAULT 0," +
                "UNIQUE(user_id, column_name)," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")");

        // 7. chat_history
        db.execSQL("CREATE TABLE chat_history (" +
                "id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id     INTEGER NOT NULL," +
                "lesson_name TEXT    NOT NULL," +
                "role        TEXT    NOT NULL," +   // 'user' or 'model'
                "message     TEXT    NOT NULL," +
                "timestamp   TEXT    NOT NULL," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")");

        // 8. test_results
        db.execSQL("CREATE TABLE test_results (" +
                "id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id     INTEGER NOT NULL," +
                "score       INTEGER NOT NULL," +
                "total       INTEGER NOT NULL," +
                "grade       TEXT    NOT NULL," +
                "mastery_pct INTEGER NOT NULL," +
                "time_taken  INTEGER NOT NULL," +   // seconds
                "taken_at    TEXT    NOT NULL," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")");

        // 9. test_question_results
        db.execSQL("CREATE TABLE test_question_results (" +
                "id             INTEGER PRIMARY KEY AUTOINCREMENT," +
                "test_id        INTEGER NOT NULL," +
                "question_num   INTEGER NOT NULL," +
                "category       TEXT    NOT NULL," +
                "was_correct    INTEGER NOT NULL," +   // 1 = true, 0 = false
                "user_answer    TEXT    NOT NULL," +
                "correct_answer TEXT    NOT NULL," +
                "FOREIGN KEY(test_id) REFERENCES test_results(id)" +
                ")");

        // 10. leaderboard
        db.execSQL("CREATE TABLE leaderboard (" +
                "user_id      INTEGER PRIMARY KEY," +
                "xp           INTEGER DEFAULT 0," +
                "rank         INTEGER DEFAULT 0," +
                "country_code TEXT    DEFAULT 'JP'," +
                "weekly_xp    INTEGER DEFAULT 0," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")");

        // Seed leaderboard with dummy opponents so it's not empty on first run
        seedLeaderboard(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS leaderboard");
        db.execSQL("DROP TABLE IF EXISTS test_question_results");
        db.execSQL("DROP TABLE IF EXISTS test_results");
        db.execSQL("DROP TABLE IF EXISTS chat_history");
        db.execSQL("DROP TABLE IF EXISTS alphabet_progress");
        db.execSQL("DROP TABLE IF EXISTS lesson_progress");
        db.execSQL("DROP TABLE IF EXISTS user_preferences");
        db.execSQL("DROP TABLE IF EXISTS user_interests");
        db.execSQL("DROP TABLE IF EXISTS user_stats");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SEEDING
    // ══════════════════════════════════════════════════════════════════════

    /** Insert 11 fake opponents so the leaderboard has data from day one. */
    private void seedLeaderboard(SQLiteDatabase db) {
        String[][] opponents = {
                // {full_name, username, xp, country_code, weekly_xp}
                {"Alex Chen",   "alex_chen",   "9800", "JP", "1200"},
                {"Yuki S.",     "yuki_s",      "9200", "JP", "1100"},
                {"Maria G.",    "maria_g",     "8700", "ES", "980"},
                {"Lena K.",     "lena_k",      "7500", "DE", "870"},
                {"Ryo T.",      "ryo_t",       "6900", "JP", "760"},
                {"Sofia P.",    "sofia_p",     "6200", "BR", "650"},
                {"James W.",    "james_w",     "5400", "US", "540"},
                {"Aiko M.",     "aiko_m",      "4800", "JP", "430"},
                {"Carlos R.",   "carlos_r",    "4100", "MX", "390"},
                {"Emma L.",     "emma_l",      "3600", "FR", "340"},
                {"Noah B.",     "noah_b",      "3000", "US", "300"},
        };

        String now = currentTimestamp();
        int rank = 1;
        for (String[] o : opponents) {
            // Insert dummy user (no real email/password needed)
            ContentValues uv = new ContentValues();
            uv.put("full_name",     o[0]);
            uv.put("username",      o[1]);
            uv.put("email",         o[1] + "@dummy.convofluent");
            uv.put("password_hash", "dummy");
            uv.put("gender",        "");
            uv.put("language",      "Japanese");
            uv.put("created_at",    now);
            long userId = db.insert("users", null, uv);

            // Insert stats
            ContentValues sv = new ContentValues();
            sv.put("user_id",     userId);
            sv.put("xp",          Integer.parseInt(o[2]));
            sv.put("level",       Integer.parseInt(o[2]) / 500);
            sv.put("rank",        rank);
            sv.put("streak",      rank * 2);
            sv.put("last_active", now);
            db.insert("user_stats", null, sv);

            // Insert leaderboard row
            ContentValues lv = new ContentValues();
            lv.put("user_id",      userId);
            lv.put("xp",           Integer.parseInt(o[2]));
            lv.put("rank",         rank);
            lv.put("country_code", o[3]);
            lv.put("weekly_xp",    Integer.parseInt(o[4]));
            db.insert("leaderboard", null, lv);

            rank++;
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  USER METHODS
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Register a new user.
     * Returns the new user's ID, or -1 if email/username already exists.
     */
    public long registerUser(String fullName, String username, String email,
                             String password, String gender, String language) {
        SQLiteDatabase db = getWritableDatabase();

        // Check uniqueness
        if (emailExists(email) || usernameExists(username)) return -1;

        String now = currentTimestamp();

        ContentValues uv = new ContentValues();
        uv.put("full_name",     fullName);
        uv.put("username",      username);
        uv.put("email",         email.toLowerCase().trim());
        uv.put("password_hash", hashPassword(password));
        uv.put("gender",        gender);
        uv.put("language",      language);
        uv.put("created_at",    now);
        long userId = db.insert("users", null, uv);
        if (userId == -1) return -1;

        // Default stats row
        ContentValues sv = new ContentValues();
        sv.put("user_id",     userId);
        sv.put("streak",      0);
        sv.put("xp",          0);
        sv.put("level",       1);
        sv.put("rank",        0);
        sv.put("last_active", now);
        db.insert("user_stats", null, sv);

        // Default preferences row
        ContentValues pv = new ContentValues();
        pv.put("user_id",         userId);
        pv.put("daily_goal_mins", 15);
        pv.put("difficulty",      "Beginner");
        db.insert("user_preferences", null, pv);

        // Default leaderboard row (starts at bottom)
        ContentValues lv = new ContentValues();
        lv.put("user_id",      userId);
        lv.put("xp",           0);
        lv.put("rank",         0);
        lv.put("country_code", "JP");
        lv.put("weekly_xp",    0);
        db.insert("leaderboard", null, lv);

        return userId;
    }

    /**
     * Verify login credentials.
     * Returns user ID on success, -1 on failure.
     */
    public long loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("users",
                new String[]{"id"},
                "email = ? AND password_hash = ?",
                new String[]{email.toLowerCase().trim(), hashPassword(password)},
                null, null, null);
        long userId = -1;
        if (c.moveToFirst()) userId = c.getLong(0);
        c.close();
        return userId;
    }

    /** Update gender and language (called from ProfileSetupActivity). */
    public void updateUserProfile(long userId, String gender, String language) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("gender",   gender);
        cv.put("language", language);
        db.update("users", cv, "id = ?", new String[]{String.valueOf(userId)});
    }

    /** Returns a User model for the given ID, or null if not found. */
    public User getUser(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("users", null,
                "id = ?", new String[]{String.valueOf(userId)},
                null, null, null);
        User user = null;
        if (c.moveToFirst()) {
            user = new User();
            user.id        = c.getLong(c.getColumnIndexOrThrow("id"));
            user.fullName  = c.getString(c.getColumnIndexOrThrow("full_name"));
            user.username  = c.getString(c.getColumnIndexOrThrow("username"));
            user.email     = c.getString(c.getColumnIndexOrThrow("email"));
            user.gender    = c.getString(c.getColumnIndexOrThrow("gender"));
            user.language  = c.getString(c.getColumnIndexOrThrow("language"));
        }
        c.close();
        return user;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("users", new String[]{"id"},
                "email = ?", new String[]{email.toLowerCase().trim()},
                null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("users", new String[]{"id"},
                "username = ?", new String[]{username},
                null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    // ── User model ─────────────────────────────────────────────────────────
    public static class User {
        public long   id;
        public String fullName, username, email, gender, language;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  STATS METHODS
    // ══════════════════════════════════════════════════════════════════════

    public UserStats getStats(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("user_stats", null,
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, null);
        UserStats s = new UserStats();
        if (c.moveToFirst()) {
            s.streak     = c.getInt(c.getColumnIndexOrThrow("streak"));
            s.xp         = c.getInt(c.getColumnIndexOrThrow("xp"));
            s.level      = c.getInt(c.getColumnIndexOrThrow("level"));
            s.rank       = c.getInt(c.getColumnIndexOrThrow("rank"));
            s.lastActive = c.getString(c.getColumnIndexOrThrow("last_active"));
        }
        c.close();
        return s;
    }

    /** Add XP and update level (every 500 XP = 1 level). Also updates leaderboard. */
    public void addXp(long userId, int amount) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query("user_stats", new String[]{"xp"},
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, null);
        int currentXp = 0;
        if (c.moveToFirst()) currentXp = c.getInt(0);
        c.close();

        int newXp    = currentXp + amount;
        int newLevel = Math.max(1, newXp / 500);

        ContentValues cv = new ContentValues();
        cv.put("xp",          newXp);
        cv.put("level",       newLevel);
        cv.put("last_active", currentTimestamp());
        db.update("user_stats", cv, "user_id = ?", new String[]{String.valueOf(userId)});

        // Sync leaderboard
        ContentValues lv = new ContentValues();
        lv.put("xp",        newXp);
        lv.put("weekly_xp", newXp); // simplified — full app would reset weekly
        db.update("leaderboard", lv, "user_id = ?", new String[]{String.valueOf(userId)});

        // Recalculate ranks for all users
        recalculateRanks(db);
    }

    /** Increment streak by 1 if last_active was yesterday; reset to 1 if missed a day. */
    public void updateStreak(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query("user_stats", new String[]{"streak", "last_active"},
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, null);

        int    currentStreak = 0;
        String lastActive    = "";
        if (c.moveToFirst()) {
            currentStreak = c.getInt(0);
            lastActive    = c.getString(1);
        }
        c.close();

        String today     = todayDate();
        String yesterday = yesterdayDate();
        int    newStreak;

        if (lastActive == null || lastActive.isEmpty()) {
            newStreak = 1;
        } else if (lastActive.startsWith(today)) {
            return; // Already updated today — do nothing
        } else if (lastActive.startsWith(yesterday)) {
            newStreak = currentStreak + 1; // Continuing streak
        } else {
            newStreak = 1; // Missed a day — reset
        }

        ContentValues cv = new ContentValues();
        cv.put("streak",      newStreak);
        cv.put("last_active", currentTimestamp());
        db.update("user_stats", cv, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    private void recalculateRanks(SQLiteDatabase db) {
        // Get all leaderboard rows ordered by XP desc, assign rank 1, 2, 3...
        Cursor c = db.query("leaderboard", new String[]{"user_id"},
                null, null, null, null, "xp DESC");
        int rank = 1;
        while (c.moveToNext()) {
            long uid = c.getLong(0);
            ContentValues cv = new ContentValues();
            cv.put("rank", rank);
            db.update("leaderboard", cv, "user_id = ?", new String[]{String.valueOf(uid)});
            db.update("user_stats",   cv, "user_id = ?", new String[]{String.valueOf(uid)});
            rank++;
        }
        c.close();
    }

    public static class UserStats {
        public int    streak, xp, level, rank;
        public String lastActive;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  INTERESTS METHODS
    // ══════════════════════════════════════════════════════════════════════

    /** Replace all interests for a user with a new list. */
    public void saveInterests(long userId, List<String> interests) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("user_interests", "user_id = ?", new String[]{String.valueOf(userId)});
        for (String interest : interests) {
            ContentValues cv = new ContentValues();
            cv.put("user_id",       userId);
            cv.put("interest_name", interest);
            db.insert("user_interests", null, cv);
        }
    }

    /** Returns list of interest names for a user. */
    public List<String> getInterests(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("user_interests", new String[]{"interest_name"},
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, null);
        List<String> list = new ArrayList<>();
        while (c.moveToNext()) list.add(c.getString(0));
        c.close();
        return list;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PREFERENCES METHODS
    // ══════════════════════════════════════════════════════════════════════

    public UserPreferences getPreferences(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("user_preferences", null,
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, null);
        UserPreferences p = new UserPreferences();
        if (c.moveToFirst()) {
            p.dailyGoalMins = c.getInt(c.getColumnIndexOrThrow("daily_goal_mins"));
            p.difficulty    = c.getString(c.getColumnIndexOrThrow("difficulty"));
        }
        c.close();
        return p;
    }

    public void savePreferences(long userId, int dailyGoalMins, String difficulty) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("daily_goal_mins", dailyGoalMins);
        cv.put("difficulty",      difficulty);
        db.update("user_preferences", cv, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public static class UserPreferences {
        public int    dailyGoalMins = 15;
        public String difficulty    = "Beginner";
    }

    // ══════════════════════════════════════════════════════════════════════
    //  LESSON PROGRESS METHODS
    // ══════════════════════════════════════════════════════════════════════

    /** Save or update lesson progress (0–100). */
    public void saveLessonProgress(long userId, String lessonName, int progressPct) {
        SQLiteDatabase db  = getWritableDatabase();
        ContentValues  cv  = new ContentValues();
        cv.put("user_id",        userId);
        cv.put("lesson_name",    lessonName);
        cv.put("progress_pct",   progressPct);
        cv.put("last_opened_at", currentTimestamp());
        // Insert or replace (UNIQUE constraint on user_id + lesson_name)
        db.insertWithOnConflict("lesson_progress", null, cv,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    /** Returns progress % for a lesson (0 if never opened). */
    public int getLessonProgress(long userId, String lessonName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("lesson_progress", new String[]{"progress_pct"},
                "user_id = ? AND lesson_name = ?",
                new String[]{String.valueOf(userId), lessonName},
                null, null, null);
        int pct = 0;
        if (c.moveToFirst()) pct = c.getInt(0);
        c.close();
        return pct;
    }

    /** Returns the lesson_name of the most recently opened lesson, or null. */
    public String getLastOpenedLesson(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("lesson_progress", new String[]{"lesson_name"},
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, "last_opened_at DESC", "1");
        String name = null;
        if (c.moveToFirst()) name = c.getString(0);
        c.close();
        return name;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ALPHABET PROGRESS METHODS
    // ══════════════════════════════════════════════════════════════════════

    /** Save current character index + mastered count for a column. */
    public void saveAlphabetProgress(long userId, String columnName,
                                     int charIndex, int masteredCount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues  cv = new ContentValues();
        cv.put("user_id",        userId);
        cv.put("column_name",    columnName);
        cv.put("char_index",     charIndex);
        cv.put("mastered_count", masteredCount);
        db.insertWithOnConflict("alphabet_progress", null, cv,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public AlphabetProgress getAlphabetProgress(long userId, String columnName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("alphabet_progress", null,
                "user_id = ? AND column_name = ?",
                new String[]{String.valueOf(userId), columnName},
                null, null, null);
        AlphabetProgress p = new AlphabetProgress();
        if (c.moveToFirst()) {
            p.charIndex     = c.getInt(c.getColumnIndexOrThrow("char_index"));
            p.masteredCount = c.getInt(c.getColumnIndexOrThrow("mastered_count"));
        }
        c.close();
        return p;
    }

    /** Total mastered characters across ALL columns for a user. */
    public int getTotalMasteredChars(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(mastered_count) FROM alphabet_progress WHERE user_id = ?",
                new String[]{String.valueOf(userId)});
        int total = 0;
        if (c.moveToFirst()) total = c.getInt(0);
        c.close();
        return total;
    }

    public static class AlphabetProgress {
        public int charIndex = 0, masteredCount = 0;
    }
    public void saveChatMessage(long userId, String lessonName,
                                String role, String message) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues  cv = new ContentValues();
        cv.put("user_id",     userId);
        cv.put("lesson_name", lessonName);
        cv.put("role",        role);        // "user" or "model"
        cv.put("message",     message);
        cv.put("timestamp",   currentTimestamp());
        db.insert("chat_history", null, cv);
    }

    /** Returns all chat messages for a lesson in chronological order. */
    public List<ChatMessage> getChatHistory(long userId, String lessonName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("chat_history",
                new String[]{"role", "message", "timestamp"},
                "user_id = ? AND lesson_name = ?",
                new String[]{String.valueOf(userId), lessonName},
                null, null, "timestamp ASC");
        List<ChatMessage> list = new ArrayList<>();
        while (c.moveToNext()) {
            ChatMessage m = new ChatMessage();
            m.role      = c.getString(0);
            m.message   = c.getString(1);
            m.timestamp = c.getString(2);
            list.add(m);
        }
        c.close();
        return list;
    }

    public void clearChatHistory(long userId, String lessonName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("chat_history",
                "user_id = ? AND lesson_name = ?",
                new String[]{String.valueOf(userId), lessonName});
    }

    public static class ChatMessage {
        public String role, message, timestamp;
    }
    public long saveTestResult(long userId, int score, int total,
                               String grade, int masteryPct, int timeTakenSeconds) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues  cv = new ContentValues();
        cv.put("user_id",     userId);
        cv.put("score",       score);
        cv.put("total",       total);
        cv.put("grade",       grade);
        cv.put("mastery_pct", masteryPct);
        cv.put("time_taken",  timeTakenSeconds);
        cv.put("taken_at",    currentTimestamp());
        return db.insert("test_results", null, cv);
    }

    /** Save one question's result tied to a test. */
    public void saveQuestionResult(long testId, int questionNum, String category,
                                   boolean wasCorrect, String userAnswer,
                                   String correctAnswer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues  cv = new ContentValues();
        cv.put("test_id",        testId);
        cv.put("question_num",   questionNum);
        cv.put("category",       category);
        cv.put("was_correct",    wasCorrect ? 1 : 0);
        cv.put("user_answer",    userAnswer);
        cv.put("correct_answer", correctAnswer);
        db.insert("test_question_results", null, cv);
    }

    /** Get the most recent test result for a user. */
    public TestResult getLatestTestResult(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("test_results", null,
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, "taken_at DESC", "1");
        TestResult r = null;
        if (c.moveToFirst()) {
            r            = new TestResult();
            r.id         = c.getLong(c.getColumnIndexOrThrow("id"));
            r.score      = c.getInt(c.getColumnIndexOrThrow("score"));
            r.total      = c.getInt(c.getColumnIndexOrThrow("total"));
            r.grade      = c.getString(c.getColumnIndexOrThrow("grade"));
            r.masteryPct = c.getInt(c.getColumnIndexOrThrow("mastery_pct"));
            r.timeTaken  = c.getInt(c.getColumnIndexOrThrow("time_taken"));
            r.takenAt    = c.getString(c.getColumnIndexOrThrow("taken_at"));
        }
        c.close();
        return r;
    }

    /** Get all question results for a specific test. */
    public List<QuestionResult> getQuestionResults(long testId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("test_question_results", null,
                "test_id = ?", new String[]{String.valueOf(testId)},
                null, null, "question_num ASC");
        List<QuestionResult> list = new ArrayList<>();
        while (c.moveToNext()) {
            QuestionResult q  = new QuestionResult();
            q.questionNum     = c.getInt(c.getColumnIndexOrThrow("question_num"));
            q.category        = c.getString(c.getColumnIndexOrThrow("category"));
            q.wasCorrect      = c.getInt(c.getColumnIndexOrThrow("was_correct")) == 1;
            q.userAnswer      = c.getString(c.getColumnIndexOrThrow("user_answer"));
            q.correctAnswer   = c.getString(c.getColumnIndexOrThrow("correct_answer"));
            list.add(q);
        }
        c.close();
        return list;
    }

    /** Returns number of tests taken by user. */
    public int getTestCount(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM test_results WHERE user_id = ?",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public static class TestResult {
        public long   id;
        public int    score, total, masteryPct, timeTaken;
        public String grade, takenAt;
    }

    public static class QuestionResult {
        public int     questionNum;
        public String  category, userAnswer, correctAnswer;
        public boolean wasCorrect;
    }

    /** Returns top N leaderboard entries joined with user info. */
    public List<LeaderboardEntry> getTopLeaderboard(int limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT u.id, u.username, u.full_name, l.xp, l.rank, " +
                        "l.country_code, l.weekly_xp, s.level " +
                        "FROM leaderboard l " +
                        "JOIN users u ON u.id = l.user_id " +
                        "JOIN user_stats s ON s.user_id = l.user_id " +
                        "ORDER BY l.xp DESC LIMIT ?",
                new String[]{String.valueOf(limit)});
        List<LeaderboardEntry> list = new ArrayList<>();
        while (c.moveToNext()) {
            LeaderboardEntry e = new LeaderboardEntry();
            e.userId      = c.getLong(0);
            e.username    = c.getString(1);
            e.fullName    = c.getString(2);
            e.xp          = c.getInt(3);
            e.rank        = c.getInt(4);
            e.countryCode = c.getString(5);
            e.weeklyXp    = c.getInt(6);
            e.level       = c.getInt(7);
            list.add(e);
        }
        c.close();
        return list;
    }

    /** Get a single user's leaderboard entry. */
    public LeaderboardEntry getMyLeaderboardEntry(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT u.id, u.username, u.full_name, l.xp, l.rank, " +
                        "l.country_code, l.weekly_xp, s.level " +
                        "FROM leaderboard l " +
                        "JOIN users u ON u.id = l.user_id " +
                        "JOIN user_stats s ON s.user_id = l.user_id " +
                        "WHERE l.user_id = ?",
                new String[]{String.valueOf(userId)});
        LeaderboardEntry e = null;
        if (c.moveToFirst()) {
            e             = new LeaderboardEntry();
            e.userId      = c.getLong(0);
            e.username    = c.getString(1);
            e.fullName    = c.getString(2);
            e.xp          = c.getInt(3);
            e.rank        = c.getInt(4);
            e.countryCode = c.getString(5);
            e.weeklyXp    = c.getInt(6);
            e.level       = c.getInt(7);
        }
        c.close();
        return e;
    }

    /** Update country code for a user (set during profile setup). */
    public void updateCountryCode(long userId, String countryCode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("country_code", countryCode);
        db.update("leaderboard", cv, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public static class LeaderboardEntry {
        public long   userId;
        public String username, fullName, countryCode;
        public int    xp, rank, weeklyXp, level;
    }

    /** SHA-256 hash a password. Returns hex string. */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return password; // fallback (should never happen)
        }
    }

    private String currentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    private String todayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
    }

    private String yesterdayDate() {
        long yesterday = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date(yesterday));
    }
}