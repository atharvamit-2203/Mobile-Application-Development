package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

/**
 * TEMPORARY ACTIVITY TO CREATE ADMIN USER
 * Use this once to set up admin, then remove or disable
 */
public class AdminSetupActivity extends Activity {
    
    private EditText emailInput;
    private EditText passwordInput;
    private EditText nameInput;
    private Button createAdminBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_setup);
        
        db = FirebaseFirestore.getInstance();
        
        emailInput = findViewById(R.id.adminEmail);
        passwordInput = findViewById(R.id.adminPassword);
        nameInput = findViewById(R.id.adminName);
        createAdminBtn = findViewById(R.id.createAdminBtn);
        
        createAdminBtn.setOnClickListener(v -> createAdmin());
    }
    
    private void createAdmin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toast.makeText(this, "Creating admin user...", Toast.LENGTH_SHORT).show();
        
        // Register with Firebase Auth
        FirebaseHelper.register(email, password, new FirebaseHelper.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                // Create admin profile in Firestore
                Map<String, Object> adminData = new HashMap<>();
                adminData.put("name", name);
                adminData.put("email", email);
                adminData.put("role", "admin");  // IMPORTANT: Set role as admin
                adminData.put("created_at", System.currentTimeMillis());
                
                db.collection("users").document(userId)
                    .set(adminData)
                    .addOnSuccessListener(aVoid -> {
                        runOnUiThread(() -> {
                            Toast.makeText(AdminSetupActivity.this, 
                                "✅ Admin user created successfully!\nEmail: " + email, 
                                Toast.LENGTH_LONG).show();
                            
                            // Clear fields
                            emailInput.setText("");
                            passwordInput.setText("");
                            nameInput.setText("");
                        });
                    })
                    .addOnFailureListener(e -> {
                        runOnUiThread(() -> {
                            Toast.makeText(AdminSetupActivity.this, 
                                "Error creating admin profile: " + e.getMessage(), 
                                Toast.LENGTH_LONG).show();
                        });
                    });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminSetupActivity.this, 
                        "Error creating admin: " + error, 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
