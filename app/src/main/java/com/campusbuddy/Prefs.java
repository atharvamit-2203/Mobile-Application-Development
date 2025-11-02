package com.campusbuddy;

import android.content.Context;
import android.content.SharedPreferences;

// Simple localStorage equivalent - now works with Firebase
public class Prefs {
    private static Prefs instance;
    private SharedPreferences prefs;

    private Prefs(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences("campusbuddy", Context.MODE_PRIVATE);
    }

    public static Prefs getInstance(Context context) {
        if (instance == null) {
            instance = new Prefs(context);
        }
        return instance;
    }

    public void setUserId(String userId) {
        prefs.edit().putString("userId", userId).apply();
    }

    public String getUserId() {
        return prefs.getString("userId", null);
    }

    public void setUserRole(String role) {
        prefs.edit().putString("userRole", role).apply();
    }

    public String getUserRole() {
        return prefs.getString("userRole", null);
    }

    public void setUserData(String email, String name) {
        prefs.edit()
            .putString("email", email)
            .putString("name", name)
            .apply();
    }
    
    public void setEmail(String email) {
        prefs.edit().putString("email", email).apply();
    }
    
    public void setName(String name) {
        prefs.edit().putString("name", name).apply();
    }

    public String getEmail() {
        return prefs.getString("email", null);
    }

    public String getName() {
        return prefs.getString("name", null);
    }

    public String getToken() {
        // For backward compatibility with ApiHelper
        return getUserId();
    }

    public boolean isLoggedIn() {
        return getUserId() != null && FirebaseHelper.getCurrentUser() != null;
    }

    public void clear() {
        prefs.edit().clear().apply();
        FirebaseHelper.logout();
    }
}
