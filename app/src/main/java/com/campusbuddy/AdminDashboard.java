package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

// Admin dashboard
public class AdminDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        TextView welcomeText = findViewById(R.id.welcomeText);
        Button btnLogout = findViewById(R.id.btnLogout);
        
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            welcomeText.setText("Admin: " + name);
        } else {
            welcomeText.setText("Admin Dashboard");
        }        // Setup card click listeners - Admin has access to everything
        findViewById(R.id.cardUsers).setOnClickListener(v -> 
            Toast.makeText(this, "Manage Users - Coming Soon", Toast.LENGTH_SHORT).show()
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
        
        findViewById(R.id.cardNotifications).setOnClickListener(v -> 
            startActivity(new Intent(this, NotificationsActivity.class))
        );
        
        findViewById(R.id.cardSettings).setOnClickListener(v ->
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show()
        );
        
        // Logout button
        btnLogout.setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }
}
