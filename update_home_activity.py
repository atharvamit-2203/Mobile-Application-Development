#!/usr/bin/env python3
# -*- coding: utf-8 -*-

home_code = '''package com.campusbuddy;

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
        Button btnSetupDatabase = findViewById(R.id.btnSetupDatabase);

        btnStudent.setOnClickListener(v -> openLogin("student"));
        btnFaculty.setOnClickListener(v -> openLogin("faculty"));
        btnOrganization.setOnClickListener(v -> openLogin("organization"));
        btnStaff.setOnClickListener(v -> openLogin("staff"));
        btnAdmin.setOnClickListener(v -> openLogin("admin"));
        btnSetupDatabase.setOnClickListener(v -> openSetup());
    }

    private void openLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }
    
    private void openSetup() {
        Intent intent = new Intent(this, FirebaseSetupActivity.class);
        startActivity(intent);
    }
}
'''

# Write the file
with open(r'D:\MyApplication16\app\src\main\java\com\campusbuddy\HomeActivity.java', 'w', encoding='utf-8') as f:
    f.write(home_code)

print("✅ HomeActivity.java updated with setup button handler")
