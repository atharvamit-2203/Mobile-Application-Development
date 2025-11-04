package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends Activity {
    private static final int RC_SIGN_IN = 9001;
    private String role;
    private EditText emailInput, passwordInput;
    private GoogleSignInClient googleSignInClient;
    
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
        
        if (role.equals("faculty") || role.equals("staff")) {
            registerLink.setText("Forgot Password?");
        } else {
            registerLink.setText("Don't have an account? Register");
        }
        
        if (role.equals("student")) {
            Toast.makeText(this, "Test: arjun.sharma@mpstme.edu.in / testpassword123", Toast.LENGTH_LONG).show();
        }
        
        loginBtn.setOnClickListener(v -> login());
        backBtn.setOnClickListener(v -> finish());
        registerLink.setOnClickListener(v -> {
            if (role.equals("faculty") || role.equals("staff")) {
                openForgotPassword();
            } else {
                openRegister();
            }
        });
    }
    
    private void openRegister() {
        Intent intent;
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
                    Prefs.getInstance(LoginActivity.this).setUserId(user.getUid());
                    Prefs.getInstance(LoginActivity.this).setUserRole(userRole);
                    Prefs.getInstance(LoginActivity.this).setUserData(email, user.getDisplayName() != null ? user.getDisplayName() : email);
                    
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    
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
    
    private void openForgotPassword() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        
        new android.app.AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("You will be signed in with Google to verify your identity. After verification, you can set a new password.")
                .setPositiveButton("Continue with Google", (dialog, which) -> {
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        com.google.firebase.auth.FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            showPasswordResetDialog(user);
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showPasswordResetDialog(com.google.firebase.auth.FirebaseUser user) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Set New Password");
        builder.setMessage("Signed in as: " + user.getEmail());
        
        final EditText newPasswordInput = new EditText(this);
        newPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordInput.setHint("Enter new password");
        newPasswordInput.setPadding(50, 30, 50, 30);
        
        builder.setView(newPasswordInput);
        
        builder.setPositiveButton("Reset Password", (dialog, which) -> {
            String newPassword = newPasswordInput.getText().toString().trim();
            if (newPassword.isEmpty() || newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Password updated successfully! Please login with your new password.", Toast.LENGTH_LONG).show();
                            // Sign out and return to login
                            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                            googleSignInClient.signOut();
                        } else {
                            Toast.makeText(this, "Password update failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            googleSignInClient.signOut();
        });
        
        builder.setCancelable(false);
        builder.show();
    }
}
