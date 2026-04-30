package com.example.convofluent;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME = "convofluent_prefs";
    private static final String KEY_USER_ID = "logged_in_user_id";
    private static final long   NO_USER     = -1L;

    /** Save user ID to mark them as logged in. */
    public static void login(Context ctx, long userId) {
        prefs(ctx).edit().putLong(KEY_USER_ID, userId).apply();
    }

    /** Clear the session — user must log in again. */
    public static void logout(Context ctx) {
        prefs(ctx).edit().putLong(KEY_USER_ID, NO_USER).apply();
    }

    /** Returns the logged-in user's ID, or -1 if no session. */
    public static long getUserId(Context ctx) {
        return prefs(ctx).getLong(KEY_USER_ID, NO_USER);
    }

    /** Returns true if a user is currently logged in. */
    public static boolean isLoggedIn(Context ctx) {
        return getUserId(ctx) != NO_USER;
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}