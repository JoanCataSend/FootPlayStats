package com.example.footplaystats.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME = "footplaystats_session";
    private static final String KEY_ROLE = "role";

    // Cache en memoria (opcional, para no leer prefs cada vez)
    private static UserRole currentRole;

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void setRole(Context context, UserRole role) {
        currentRole = role;

        prefs(context)
                .edit()
                .putString(KEY_ROLE, role.name())
                .apply();
    }

    public static UserRole getRole(Context context) {
        if (currentRole != null) return currentRole;

        String stored = prefs(context).getString(KEY_ROLE, null);
        if (stored == null) return null;

        try {
            currentRole = UserRole.valueOf(stored);
            return currentRole;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isCoach(Context context) {
        UserRole role = getRole(context);
        return role == UserRole.COACH;
    }

    public static void clearSession(Context context) {
        currentRole = null;

        prefs(context)
                .edit()
                .remove(KEY_ROLE)
                .apply();
    }
}