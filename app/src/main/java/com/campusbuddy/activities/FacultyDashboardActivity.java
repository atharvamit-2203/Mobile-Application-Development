package com.campusbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.campusbuddy.*;

public class FacultyDashboardActivity extends AppCompatActivity {
    private TextView welcomeText;
    private CardView cardBookRoom, cardStudents, cardExtraClasses, cardAssignments;
    private CardView cardTimetable, cardCanteen, cardEvents, cardNotifications, cardProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        welcomeText = findViewById(R.id.welcomeText);
        
        // Faculty-specific cards
        cardBookRoom = findViewById(R.id.cardBookRoom);
        cardStudents = findViewById(R.id.cardStudents);
        cardExtraClasses = findViewById(R.id.cardExtraClasses);
        cardAssignments = findViewById(R.id.cardAssignments);
        
        // Common cards
        cardTimetable = findViewById(R.id.cardTimetable);
        cardCanteen = findViewById(R.id.cardCanteen);
        cardEvents = findViewById(R.id.cardEvents);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardProfile = findViewById(R.id.cardProfile);
        
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            welcomeText.setText("Welcome, " + name + "!");
        } else {
            welcomeText.setText("Welcome, Faculty!");
        }
        
        // Setup logout button
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            FirebaseHelper.logout();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private void setupClickListeners() {
        // Faculty-specific features
        cardBookRoom.setOnClickListener(v -> 
            startActivity(new Intent(this, FacultyRoomBookingActivity.class)));
        
        cardStudents.setOnClickListener(v -> 
            Toast.makeText(this, "My Students feature coming soon!", Toast.LENGTH_SHORT).show());
        
        cardExtraClasses.setOnClickListener(v -> 
            Toast.makeText(this, "Extra Classes feature coming soon!", Toast.LENGTH_SHORT).show());
        
        cardAssignments.setOnClickListener(v -> 
            Toast.makeText(this, "Assignments feature coming soon!", Toast.LENGTH_SHORT).show());
        
        // Common features
        cardTimetable.setOnClickListener(v -> 
            startActivity(new Intent(this, TimetableActivity.class)));
        
        cardCanteen.setOnClickListener(v -> 
            startActivity(new Intent(this, CanteenActivity.class)));
        
        cardEvents.setOnClickListener(v -> 
            startActivity(new Intent(this, EventsActivity.class)));
        
        cardNotifications.setOnClickListener(v -> 
            startActivity(new Intent(this, NotificationsActivity.class)));
        
        cardProfile.setOnClickListener(v -> 
            startActivity(new Intent(this, ProfileActivity.class)));
    }
}
