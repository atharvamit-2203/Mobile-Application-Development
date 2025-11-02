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
    }

    private void openLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }
}
