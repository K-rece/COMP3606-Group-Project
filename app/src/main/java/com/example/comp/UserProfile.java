package com.example.comp;

import android.content.Context;

/** Saves the user's name so the SMS layer (which runs outside any screen) can use it. */
public final class UserProfile {
    private static final String PREFS = "assignment_buddy_prefs";
    private static final String KEY_NAME = "user_name";

    private UserProfile() {}

    public static void saveName(Context ctx, String name) {
        ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_NAME, name == null ? "" : name.trim()).apply();
    }

    /** Returns the saved name, or "Me" if none has been saved yet. */
    public static String getName(Context ctx) {
        String n = ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_NAME, "");
        return n.isEmpty() ? "Me" : n;
    }
}
