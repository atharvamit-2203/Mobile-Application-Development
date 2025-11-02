package com.campusbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    
    private TextInputEditText fullNameInput, usernameInput, emailInput, collegeIdInput, passwordInput, confirmPasswordInput;
    private AutoCompleteTextView courseSpinner, semesterSpinner;
    private MaterialButton registerButton;
    private TextView loginLink;
    
    private String userRole = "student"; // Default role
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Get user role from intent (student, faculty, organization, admin, staff)
        userRole = getIntent().getStringExtra("role");
        if (userRole == null) userRole = "student";
        
        initViews();
        setupDropdowns();
        setupListeners();
    }
    
    private void initViews() {
        fullNameInput = findViewById(R.id.fullNameInput);
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        collegeIdInput = findViewById(R.id.collegeIdInput);
        courseSpinner = findViewById(R.id.courseSpinner);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
    }
    
    private void setupDropdowns() {
        // Course dropdown (matching React app)
        String[] courses = {"MBA TECH", "B TECH CE", "B TECH AIDS"};
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, courses);
        courseSpinner.setAdapter(courseAdapter);
        
        // Semester dropdown (matching React app)
        String[] semesters = {"1", "3", "5", "7"};
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, semesters);
        semesterSpinner.setAdapter(semesterAdapter);
    }
    
    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleRegister());
        loginLink.setOnClickListener(v -> {
            finish(); // Go back to login
        });
    }
    
    private void handleRegister() {
        // Get input values
        String fullName = fullNameInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String collegeId = collegeIdInput.getText().toString().trim();
        String course = courseSpinner.getText().toString().trim();
        String semester = semesterSpinner.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        
        // Validate inputs
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || 
            collegeId.isEmpty() || course.isEmpty() || semester.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check password match (matching React app validation)
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match. Please enter the same password.", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Show loading state
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");
        
        // Register with Firebase (matching React app's register function)
        FirebaseHelper.register(email, password, new FirebaseHelper.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                // After Firebase Auth success, create user document in Firestore
                createUserProfile(userId, fullName, username, email, collegeId, course, semester);
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                });
            }
        });
    }
    
    private void createUserProfile(String userId, String fullName, String username, String email, 
                                   String collegeId, String course, String semester) {
        // Create user profile matching backend User model
        java.util.Map<String, Object> userData = new java.util.HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("full_name", fullName);
        userData.put("role", userRole);
        userData.put("college_id", collegeId);
        
        // Student specific fields (matching backend model)
        if (userRole.equals("student")) {
            userData.put("student_id", collegeId); // Use college_id as student_id for now
            userData.put("course", course);
            userData.put("semester", semester);
            userData.put("branch", course); // Map course to branch
            userData.put("academic_year", "2024-25");
            userData.put("batch", "2024"); // Default, can be updated later
        }
        
        // Common fields
        userData.put("is_active", true);
        userData.put("is_verified", false);
        userData.put("created_at", System.currentTimeMillis());
        userData.put("updated_at", System.currentTimeMillis());
        
        // Save to Firestore
        FirebaseHelper.createUserProfile(userId, userData, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(java.util.Map<String, Object> data) {
                runOnUiThread(() -> {
                    // Save user data to preferences
                    Prefs prefs = Prefs.getInstance(RegisterActivity.this);
                    prefs.setUserId(userId);
                    prefs.setName(fullName);
                    prefs.setEmail(email);
                    prefs.setUserRole(userRole);
                    
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to appropriate dashboard based on role
                    navigateToDashboard(userRole);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Failed to create profile: " + error, Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                });
            }
        });
    }
    
    private void navigateToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "student":
                intent = new Intent(this, StudentDashboard.class);
                break;
            case "faculty":
                intent = new Intent(this, FacultyDashboard.class);
                break;
            case "organization":
                intent = new Intent(this, OrganizationDashboard.class);
                break;
            case "staff":
                intent = new Intent(this, StaffDashboard.class);
                break;
            case "admin":
                intent = new Intent(this, AdminDashboard.class);
                break;
            default:
                intent = new Intent(this, StudentDashboard.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
