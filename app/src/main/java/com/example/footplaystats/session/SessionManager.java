package com.example.footplaystats.session;

public class SessionManager {

    private static UserRole currentRole;

    public static void setRole(UserRole role) {
        currentRole = role;
    }

    public static UserRole getRole() {
        return currentRole;
    }

    public static boolean isCoach() {
        return currentRole == UserRole.COACH;
    }

    public static void clearSession() {
        currentRole = null;
    }
}