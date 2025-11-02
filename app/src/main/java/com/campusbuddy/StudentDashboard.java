package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// Student dashboard with features
public class StudentDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            welcomeText.setText("Welcome, " + name + "!");
        } else {
            welcomeText.setText("Welcome, Student!");
        }
        
        // Logout button
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            FirebaseHelper.logout();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        
        // Setup card click listeners
        findViewById(R.id.cardCanteen).setOnClickListener(v -> 
            startActivity(new Intent(this, CanteenActivity.class))
        );
        
        findViewById(R.id.cardClubs).setOnClickListener(v -> 
            startActivity(new Intent(this, ClubsActivity.class))
        );
        
        findViewById(R.id.cardEvents).setOnClickListener(v -> 
            startActivity(new Intent(this, EventsActivity.class))
        );
        
        findViewById(R.id.cardBookings).setOnClickListener(v -> 
            startActivity(new Intent(this, BookingsActivity.class))
        );
        
        findViewById(R.id.cardProfile).setOnClickListener(v -> 
            startActivity(new Intent(this, ProfileActivity.class))
        );
        
        findViewById(R.id.cardNotifications).setOnClickListener(v -> 
            startActivity(new Intent(this, NotificationsActivity.class))
        );
        
        findViewById(R.id.cardTimetable).setOnClickListener(v -> 
            startActivity(new Intent(this, TimetableActivity.class))
        );
        
        // Logout - add long press to welcome text
        welcomeText.setOnLongClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });
    }
}
