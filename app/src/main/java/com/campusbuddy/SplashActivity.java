package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

// Splash screen that shows logo and checks login
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (Prefs.getInstance(this).isLoggedIn()) {
                // User logged in, go to dashboard
                String role = Prefs.getInstance(this).getUserRole();
                Intent intent = getDashboardIntent(role);
                startActivity(intent);
            } else {
                // Not logged in, go to home
                startActivity(new Intent(this, HomeActivity.class));
            }
            finish();
        }, 2000);
    }
    
    private Intent getDashboardIntent(String role) {
        if (role == null) role = "student";
        switch (role.toLowerCase()) {
            case "student": return new Intent(this, StudentDashboard.class);
            case "faculty": return new Intent(this, FacultyDashboard.class);
            case "organization": return new Intent(this, OrganizationDashboard.class);
            case "staff": return new Intent(this, StaffDashboard.class);
            case "admin": return new Intent(this, AdminDashboard.class);
            default: return new Intent(this, HomeActivity.class);
        }
    }
}
