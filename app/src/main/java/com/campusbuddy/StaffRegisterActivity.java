package com.campusbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StaffRegisterActivity extends AppCompatActivity {
    
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText sapIdInput;
    private AutoCompleteTextView departmentSpinner;
    private AutoCompleteTextView positionSpinner;
    private TextInputEditText phoneInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private Button registerButton;
    private TextView loginLink;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_register);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Initialize views
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        sapIdInput = findViewById(R.id.sapIdInput);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        positionSpinner = findViewById(R.id.positionSpinner);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        
        setupDropdowns();
        
        registerButton.setOnClickListener(v -> handleRegister());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
    
    private void setupDropdowns() {
        // Department dropdown
        String[] departments = {
            "Computer Science",
            "Information Technology",
            "Electronics",
            "Mechanical",
            "Civil",
            "Electrical",
            "Mathematics",
            "Physics",
            "Chemistry",
            "Management",
            "Library",
            "Administration",
            "Canteen",
            "Security",
            "Maintenance"
        };
        
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            departments
        );
        departmentSpinner.setAdapter(deptAdapter);
        
        // Position dropdown
        String[] positions = {
            "Security Staff",
            "Maintenance Staff",
            "Canteen Staff",
            "Lab Assistant",
            "Administrative Staff",
            "Library Staff",
            "IT Support",
            "Housekeeping",
            "Transport Staff",
            "Other"
        };
        
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            positions
        );
        positionSpinner.setAdapter(posAdapter);
    }
    
    private void handleRegister() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String sapId = sapIdInput.getText().toString().trim();
        String department = departmentSpinner.getText().toString().trim();
        String position = positionSpinner.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        
        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }
        
        if (TextUtils.isEmpty(sapId)) {
            sapIdInput.setError("SAP ID is required");
            return;
        }
        
        if (TextUtils.isEmpty(department)) {
            departmentSpinner.setError("Department is required");
            return;
        }
        
        if (TextUtils.isEmpty(position)) {
            positionSpinner.setError("Position is required");
            return;
        }
        
        if (TextUtils.isEmpty(phone)) {
            phoneInput.setError("Phone number is required");
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }
        
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }
        
        // Show loading
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");
        
        // Create Firebase Auth user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = mAuth.getCurrentUser().getUid();
                    createStaffProfile(userId, name, email, sapId, department, position, phone);
                } else {
                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                    Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), 
                        Toast.LENGTH_LONG).show();
                }
            });
    }
    
    private void createStaffProfile(String userId, String name, String email, String sapId, 
                                   String department, String position, String phone) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("sap_id", sapId);
        user.put("department", department);
        user.put("position", position);
        user.put("phone", phone);
        user.put("role", "staff");
        user.put("created_at", System.currentTimeMillis());
        
        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener(aVoid -> {
                // Save to Prefs
                Prefs.getInstance(this).setUserId(userId);
                Prefs.getInstance(this).setName(name);
                Prefs.getInstance(this).setEmail(email);
                Prefs.getInstance(this).setUserRole("staff");
                
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                
                // Navigate to Staff Dashboard
                Intent intent = new Intent(this, StaffDashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .addOnFailureListener(e -> {
                registerButton.setEnabled(true);
                registerButton.setText("Register");
                Toast.makeText(this, "Failed to create profile: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            });
    }
}
