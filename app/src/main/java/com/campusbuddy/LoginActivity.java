package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Login screen
public class LoginActivity extends Activity {
    private String role;
    private EditText emailInput, passwordInput;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        role = getIntent().getStringExtra("role");
        if (role == null) role = "student";
        
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginBtn = findViewById(R.id.loginBtn);
        Button backBtn = findViewById(R.id.backBtn);
        android.widget.TextView registerLink = findViewById(R.id.registerLink);
        
        // Show sample credentials for testing
        if (role.equals("student")) {
            Toast.makeText(this, "Test: arjun.sharma@mpstme.edu.in / testpassword123", Toast.LENGTH_LONG).show();
        }
        
        loginBtn.setOnClickListener(v -> login());
        backBtn.setOnClickListener(v -> finish());
        registerLink.setOnClickListener(v -> openRegister());
    }
    
    private void openRegister() {
        Intent intent;
        // Route to role-specific registration activities
        switch (role) {
            case "faculty":
                intent = new Intent(this, FacultyRegisterActivity.class);
                break;
            case "staff":
                intent = new Intent(this, StaffRegisterActivity.class);
                break;
            case "organization":
                intent = new Intent(this, OrganizationRegisterActivity.class);
                break;
            case "student":
                intent = new Intent(this, RegisterActivity.class);
                break;
            case "admin":
                // Admin cannot register via app
                Toast.makeText(this, "Admin registration is not available. Contact system administrator.", Toast.LENGTH_LONG).show();
                return;
            default:
                intent = new Intent(this, RegisterActivity.class);
                break;
        }
        intent.putExtra("role", role);
        startActivity(intent);
    }
    
    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
        
        FirebaseHelper.login(email, password, new FirebaseHelper.LoginCallback() {
            @Override
            public void onSuccess(com.google.firebase.auth.FirebaseUser user, String userRole) {
                runOnUiThread(() -> {
                    // Save to prefs
                    Prefs.getInstance(LoginActivity.this).setUserId(user.getUid());
                    Prefs.getInstance(LoginActivity.this).setUserRole(userRole);
                    Prefs.getInstance(LoginActivity.this).setUserData(email, user.getDisplayName() != null ? user.getDisplayName() : email);
                    
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to dashboard
                    Intent intent = getDashboardIntent(userRole);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private Intent getDashboardIntent(String role) {
        switch (role.toLowerCase()) {
            case "student": return new Intent(this, StudentDashboard.class);
            case "faculty": return new Intent(this, FacultyDashboard.class);
            case "organization": return new Intent(this, OrganizationDashboard.class);
            case "staff": return new Intent(this, StaffDashboard.class);
            case "admin": return new Intent(this, AdminDashboard.class);
            default: return new Intent(this, StudentDashboard.class);
        }
    }
}
