package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

// Staff dashboard for canteen
public class StaffDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        TextView userNameText = findViewById(R.id.userNameText);
        Button btnLogout = findViewById(R.id.btnLogout);
        
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            userNameText.setText("Welcome, " + name + "!");
        } else {
            userNameText.setText("Welcome back!");
        }

        // Setup card click listeners
        findViewById(R.id.cardOrders).setOnClickListener(v ->
            startActivity(new Intent(this, StaffOrdersActivity.class))
        );
        
        findViewById(R.id.cardScanQR).setOnClickListener(v ->
            startActivity(new Intent(this, StaffOrderScannerActivity.class))
        );

        findViewById(R.id.cardMenu).setOnClickListener(v ->
            Toast.makeText(this, "Menu Management - Coming Soon", Toast.LENGTH_SHORT).show()       
        );

        findViewById(R.id.cardInventory).setOnClickListener(v ->
            Toast.makeText(this, "Inventory - Coming Soon", Toast.LENGTH_SHORT).show()
        );
        
        findViewById(R.id.cardNotifications).setOnClickListener(v ->
            startActivity(new Intent(this, NotificationsActivity.class))
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