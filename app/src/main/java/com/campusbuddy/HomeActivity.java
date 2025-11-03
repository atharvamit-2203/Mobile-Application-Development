package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

// Home page with role selection buttons
public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnStudent = findViewById(R.id.btnStudent);
        Button btnFaculty = findViewById(R.id.btnFaculty);
        Button btnOrganization = findViewById(R.id.btnOrganization);
        Button btnStaff = findViewById(R.id.btnStaff);
        Button btnAdmin = findViewById(R.id.btnAdmin);

        btnStudent.setOnClickListener(v -> openLogin("student"));
        btnFaculty.setOnClickListener(v -> openLogin("faculty"));
        btnOrganization.setOnClickListener(v -> openLogin("organization"));
        btnStaff.setOnClickListener(v -> openLogin("staff"));
        btnAdmin.setOnClickListener(v -> openLogin("admin"));
        
        // TEMPORARY: Long press Admin button to access Admin Setup
        btnAdmin.setOnLongClickListener(v -> {
            startActivity(new Intent(this, AdminSetupActivity.class));
            return true;
        });
        
        // TEMPORARY FIX: Double-tap Admin button to fix admin role in Firestore
        final long[] lastAdminClickTime = {0};
        btnAdmin.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAdminClickTime[0] < 500) {
                // Double click detected - fix admin role
                fixAdminRole();
            } else {
                // Single click - normal login
                lastAdminClickTime[0] = currentTime;
                openLogin("admin");
            }
        });
    }
    
    private void fixAdminRole() {
        String adminEmail = "admin@mpstme.edu.in";
        android.widget.Toast.makeText(this, "Fixing admin role...", android.widget.Toast.LENGTH_SHORT).show();
        
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("email", adminEmail)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (querySnapshot.isEmpty()) {
                    android.widget.Toast.makeText(this, "Admin user not found. Please login first.", android.widget.Toast.LENGTH_LONG).show();
                } else {
                    querySnapshot.getDocuments().get(0).getReference()
                        .update("role", "admin")
                        .addOnSuccessListener(aVoid -> {
                            android.widget.Toast.makeText(this, "✅ Admin role fixed! Now login again.", android.widget.Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            android.widget.Toast.makeText(this, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                        });
                }
            })
            .addOnFailureListener(e -> {
                android.widget.Toast.makeText(this, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            });
    }

    private void openLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }
}
