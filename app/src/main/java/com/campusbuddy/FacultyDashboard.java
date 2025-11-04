package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// Faculty dashboard
public class FacultyDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            welcomeText.setText("Welcome, " + name + "!");
        } else {
            welcomeText.setText("Welcome, Faculty!");
        }
        
        // Setup faculty-specific card click listeners
        findViewById(R.id.cardBookRoom).setOnClickListener(v -> 
            startActivity(new Intent(this, FacultyRoomBookingActivity.class))
        );
        
        findViewById(R.id.cardStudents).setOnClickListener(v -> 
            startActivity(new Intent(this, FacultyStudentsActivity.class))
        );

        // Attendance Scanner
        findViewById(R.id.cardAttendance).setOnClickListener(v -> 
            startActivity(new Intent(this, FacultyAttendanceScannerActivity.class))
        );
        
        // Setup common card click listeners
        findViewById(R.id.cardTimetable).setOnClickListener(v -> 
            startActivity(new Intent(this, TimetableActivity.class))
        );
        
        findViewById(R.id.cardCanteen).setOnClickListener(v -> 
            startActivity(new Intent(this, CanteenActivity.class))
        );
        
        findViewById(R.id.cardEvents).setOnClickListener(v -> 
            startActivity(new Intent(this, EventsActivity.class))
        );
        
        findViewById(R.id.cardNotifications).setOnClickListener(v -> 
            startActivity(new Intent(this, NotificationsActivity.class))
        );
        
        findViewById(R.id.cardProfile).setOnClickListener(v -> 
            startActivity(new Intent(this, ProfileActivity.class))
        );
        
        // Logout button
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            FirebaseHelper.logout();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
