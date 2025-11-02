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

public class FacultyRegisterActivity extends AppCompatActivity {
    
    private TextInputEditText nameInput, emailInput, sapIdInput, qualificationInput, phoneInput, passwordInput, confirmPasswordInput;
    private AutoCompleteTextView departmentSpinner;
    private MaterialButton registerButton;
    private TextView loginLink;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_register);
        
        initViews();
        setupDropdowns();
        setupListeners();
    }
    
    private void initViews() {
        try {
            nameInput = findViewById(R.id.nameInput);
            emailInput = findViewById(R.id.emailInput);
            sapIdInput = findViewById(R.id.sapIdInput);
            departmentSpinner = findViewById(R.id.departmentSpinner);
            qualificationInput = findViewById(R.id.qualificationInput);
            phoneInput = findViewById(R.id.phoneInput);
            passwordInput = findViewById(R.id.passwordInput);
            confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
            registerButton = findViewById(R.id.registerButton);
            loginLink = findViewById(R.id.loginLink);
            
            if (nameInput == null || emailInput == null || registerButton == null) {
                Toast.makeText(this, "Error initializing form. Please restart.", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }
    
    private void setupDropdowns() {
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
            "Management"
        };
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, departments);
        departmentSpinner.setAdapter(deptAdapter);
    }
    
    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleRegister());
        loginLink.setOnClickListener(v -> finish());
    }
    
    private void handleRegister() {
        String name, email, sapId, department, qualification, phone, password, confirmPassword;
        
        try {
            name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
            email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            sapId = sapIdInput.getText() != null ? sapIdInput.getText().toString().trim() : "";
            department = departmentSpinner.getText() != null ? departmentSpinner.getText().toString().trim() : "";
            qualification = qualificationInput.getText() != null ? qualificationInput.getText().toString().trim() : "";
            phone = phoneInput.getText() != null ? phoneInput.getText().toString().trim() : "";
            password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";
            confirmPassword = confirmPasswordInput.getText() != null ? confirmPasswordInput.getText().toString() : "";
        } catch (Exception e) {
            Toast.makeText(this, "Error reading form data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }
        
        // Validate
        if (name.isEmpty() || email.isEmpty() || sapId.isEmpty() || department.isEmpty() || 
            qualification.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");
        
        FirebaseHelper.register(email, password, new FirebaseHelper.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                createFacultyProfile(userId, name, email, sapId, department, qualification, phone);
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(FacultyRegisterActivity.this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                });
            }
        });
    }
    
    private void createFacultyProfile(String userId, String name, String email, String sapId, 
                                     String department, String qualification, String phone) {
        java.util.Map<String, Object> userData = new java.util.HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("role", "faculty");
        userData.put("sap_id", sapId);
        userData.put("department", department);
        userData.put("qualification", qualification);
        userData.put("phone", phone);
        userData.put("is_active", true);
        userData.put("created_at", System.currentTimeMillis());
        
        FirebaseHelper.createUserProfile(userId, userData, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(java.util.Map<String, Object> data) {
                runOnUiThread(() -> {
                    Prefs prefs = Prefs.getInstance(FacultyRegisterActivity.this);
                    prefs.setUserId(userId);
                    prefs.setName(name);
                    prefs.setEmail(email);
                    prefs.setUserRole("faculty");
                    
                    Toast.makeText(FacultyRegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(FacultyRegisterActivity.this, FacultyDashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(FacultyRegisterActivity.this, "Failed to create profile: " + error, Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                });
            }
        });
    }
}
