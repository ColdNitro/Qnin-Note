package com.qnin.note;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class PasswordLockManager {
    private static final String PREFS = "qnin_prefs";
    private static final String KEY_HASH = "password_hash";
    private static final String KEY_ENABLED = "password_lock_enabled";

    public static boolean isEnabled(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false);
    }

    public static void setPassword(Context ctx, String password) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_HASH, hash(password)).putBoolean(KEY_ENABLED, true).apply();
    }

    public static boolean checkPassword(Context ctx, String password) {
        String stored = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_HASH, "");
        return stored.equals(hash(password));
    }

    public static void disable(Context ctx) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(KEY_HASH).putBoolean(KEY_ENABLED, false).apply();
    }

    private static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
