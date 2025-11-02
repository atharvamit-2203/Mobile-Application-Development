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

public class OrganizationRegisterActivity extends AppCompatActivity {
    
    private TextInputEditText orgNameInput;
    private TextInputEditText emailInput;
    private AutoCompleteTextView typeSpinner;
    private TextInputEditText descriptionInput;
    private TextInputEditText contactPersonInput;
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
        setContentView(R.layout.activity_organization_register);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Initialize views
        orgNameInput = findViewById(R.id.orgNameInput);
        emailInput = findViewById(R.id.emailInput);
        typeSpinner = findViewById(R.id.typeSpinner);
        descriptionInput = findViewById(R.id.descriptionInput);
        contactPersonInput = findViewById(R.id.contactPersonInput);
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
        // Organization type dropdown
        String[] types = {
            "Technical Club",
            "Cultural Club",
            "Sports Club",
            "Social Society",
            "Academic Society",
            "Literary Society",
            "Art & Music Club",
            "Environmental Club",
            "Entrepreneurship Cell",
            "Student Council",
            "Other"
        };
        
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            types
        );
        typeSpinner.setAdapter(typeAdapter);
    }
    
    private void handleRegister() {
        String orgName = orgNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String type = typeSpinner.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String contactPerson = contactPersonInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        
        // Validate inputs
        if (TextUtils.isEmpty(orgName)) {
            orgNameInput.setError("Organization name is required");
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }
        
        if (TextUtils.isEmpty(type)) {
            typeSpinner.setError("Organization type is required");
            return;
        }
        
        if (TextUtils.isEmpty(description)) {
            descriptionInput.setError("Description is required");
            return;
        }
        
        if (TextUtils.isEmpty(contactPerson)) {
            contactPersonInput.setError("Contact person name is required");
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
                    createOrganizationProfile(userId, orgName, email, type, description, contactPerson, phone);
                } else {
                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                    Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), 
                        Toast.LENGTH_LONG).show();
                }
            });
    }
    
    private void createOrganizationProfile(String userId, String orgName, String email, String type,
                                          String description, String contactPerson, String phone) {
        Map<String, Object> user = new HashMap<>();
        user.put("organization_name", orgName);
        user.put("name", orgName); // For compatibility
        user.put("email", email);
        user.put("type", type);
        user.put("description", description);
        user.put("contact_person", contactPerson);
        user.put("phone", phone);
        user.put("role", "organization");
        user.put("created_at", System.currentTimeMillis());
        user.put("member_count", 0);
        user.put("event_count", 0);
        
        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener(aVoid -> {
                // Also save to clubs collection for compatibility with ClubsActivity
                Map<String, Object> clubData = new HashMap<>();
                clubData.put("name", orgName);
                clubData.put("description", description);
                clubData.put("category", type);
                clubData.put("type", type);
                clubData.put("email", email);
                clubData.put("contact_person", contactPerson);
                clubData.put("phone", phone);
                clubData.put("organizationId", userId);
                clubData.put("member_count", 0);
                clubData.put("max_members", 100); // Default max members
                clubData.put("created_at", System.currentTimeMillis());
                clubData.put("is_active", true);
                
                db.collection("clubs").document(userId)
                    .set(clubData)
                    .addOnFailureListener(e -> {
                        // Non-critical error, just log it
                        Toast.makeText(this, "Warning: Club listing not created", Toast.LENGTH_SHORT).show();
                    });
                
                // Save to Prefs
                Prefs.getInstance(this).setUserId(userId);
                Prefs.getInstance(this).setName(orgName);
                Prefs.getInstance(this).setEmail(email);
                Prefs.getInstance(this).setUserRole("organization");
                
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                
                // Navigate to Organization Dashboard
                Intent intent = new Intent(this, OrganizationDashboard.class);
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
