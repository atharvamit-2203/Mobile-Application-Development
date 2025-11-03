package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class FacultyRegisterWithGoogleActivity extends Activity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private Button btnGoogleSignIn, btnBack;
    private EditText etDepartment, etSpecialization, etEmployeeId;
    private LinearLayout formLayout;
    private TextView tvEmail, tvName;
    private String googleEmail, googleName;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ScrollView scroll = new ScrollView(this);
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(40, 40, 40, 40);
        
        db = FirebaseFirestore.getInstance();
        
        TextView title = new TextView(this);
        title.setText("Faculty Registration");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 30);
        mainLayout.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("Register using your institutional Google account");
        subtitle.setTextSize(14);
        subtitle.setPadding(0, 0, 0, 30);
        mainLayout.addView(subtitle);
        
        btnGoogleSignIn = new Button(this);
        btnGoogleSignIn.setText("Sign In with Google");
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        mainLayout.addView(btnGoogleSignIn);
        
        formLayout = new LinearLayout(this);
        formLayout.setOrientation(LinearLayout.VERTICAL);
        formLayout.setVisibility(LinearLayout.GONE);
        
        TextView lblInfo = new TextView(this);
        lblInfo.setText("Complete Your Profile");
        lblInfo.setTextSize(18);
        lblInfo.setPadding(0, 30, 0, 20);
        formLayout.addView(lblInfo);
        
        tvName = new TextView(this);
        tvName.setPadding(0, 0, 0, 10);
        formLayout.addView(tvName);
        
        tvEmail = new TextView(this);
        tvEmail.setPadding(0, 0, 0, 20);
        formLayout.addView(tvEmail);
        
        etEmployeeId = new EditText(this);
        etEmployeeId.setHint("Employee ID");
        etEmployeeId.setPadding(30, 30, 30, 30);
        formLayout.addView(etEmployeeId);
        
        etDepartment = new EditText(this);
        etDepartment.setHint("Department (e.g., Computer Science)");
        etDepartment.setPadding(30, 30, 30, 30);
        formLayout.addView(etDepartment);
        
        etSpecialization = new EditText(this);
        etSpecialization.setHint("Specialization (e.g., AI, Database)");
        etSpecialization.setPadding(30, 30, 30, 30);
        formLayout.addView(etSpecialization);
        
        Button btnComplete = new Button(this);
        btnComplete.setText("Complete Registration");
        btnComplete.setOnClickListener(v -> completeRegistration());
        formLayout.addView(btnComplete);
        
        mainLayout.addView(formLayout);
        
        btnBack = new Button(this);
        btnBack.setText("Back");
        btnBack.setOnClickListener(v -> finish());
        mainLayout.addView(btnBack);
        
        scroll.addView(mainLayout);
        setContentView(scroll);
        
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleEmail = account.getEmail();
                googleName = account.getDisplayName();
                
                tvName.setText("Name: " + googleName);
                tvEmail.setText("Email: " + googleEmail);
                
                formLayout.setVisibility(LinearLayout.VISIBLE);
                btnGoogleSignIn.setEnabled(false);
                
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void completeRegistration() {
        String employeeId = etEmployeeId.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();
        
        if (employeeId.isEmpty() || department.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Map<String, Object> facultyData = new HashMap<>();
        facultyData.put("name", googleName);
        facultyData.put("email", googleEmail);
        facultyData.put("employee_id", employeeId);
        facultyData.put("department", department);
        facultyData.put("specialization", specialization);
        facultyData.put("role", "faculty");
        facultyData.put("created_at", new Date());
        facultyData.put("courses", new ArrayList<>());
        
        db.collection("faculty").document(employeeId)
            .set(facultyData)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}
