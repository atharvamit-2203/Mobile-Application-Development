#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os

# Fix SplashActivity.java
splash_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

// Splash screen that shows logo and checks login
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        new Handler().postDelayed(() -> {
            if (Prefs.getInstance(this).isLoggedIn()) {
                // User logged in, go to dashboard
                String role = Prefs.getInstance(this).getUserRole();
                Intent intent = getDashboardIntent(role);
                startActivity(intent);
            } else {
                // Not logged in, go to home
                startActivity(new Intent(this, HomeActivity.class));
            }
            finish();
        }, 2000);
    }
    
    private Intent getDashboardIntent(String role) {
        if (role == null) role = "student";
        switch (role.toLowerCase()) {
            case "student": return new Intent(this, StudentDashboard.class);
            case "faculty": return new Intent(this, FacultyDashboard.class);
            case "organization": return new Intent(this, OrganizationDashboard.class);
            case "staff": return new Intent(this, StaffDashboard.class);
            case "admin": return new Intent(this, AdminDashboard.class);
            default: return new Intent(this, HomeActivity.class);
        }
    }
}
'''

# Fix StudentDashboard.java
student_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Student dashboard with features
public class StudentDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome, " + Prefs.getInstance(this).getName());
        
        Button btnClubs = findViewById(R.id.btnClubs);
        Button btnEvents = findViewById(R.id.btnEvents);
        Button btnCanteen = findViewById(R.id.btnCanteen);
        Button btnAttendance = findViewById(R.id.btnAttendance);
        Button btnLogout = findViewById(R.id.btnLogout);
        
        btnClubs.setOnClickListener(v -> {
            Toast.makeText(this, "Clubs feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnEvents.setOnClickListener(v -> {
            Toast.makeText(this, "Events feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnCanteen.setOnClickListener(v -> {
            Toast.makeText(this, "Canteen orders coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnAttendance.setOnClickListener(v -> {
            Toast.makeText(this, "Attendance feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnLogout.setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
'''

# Fix FacultyDashboard.java
faculty_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Faculty dashboard
public class FacultyDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome, " + Prefs.getInstance(this).getName());
        
        Button btnClassSchedule = findViewById(R.id.btnClassSchedule);
        Button btnAttendance = findViewById(R.id.btnAttendance);
        Button btnGrades = findViewById(R.id.btnGrades);
        Button btnAnnouncements = findViewById(R.id.btnAnnouncements);
        Button btnLogout = findViewById(R.id.btnLogout);
        
        btnClassSchedule.setOnClickListener(v -> {
            Toast.makeText(this, "Class schedule coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnAttendance.setOnClickListener(v -> {
            Toast.makeText(this, "Attendance management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnGrades.setOnClickListener(v -> {
            Toast.makeText(this, "Grade management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnAnnouncements.setOnClickListener(v -> {
            Toast.makeText(this, "Announcements coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnLogout.setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
'''

# Fix OrganizationDashboard.java
org_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Organization dashboard
public class OrganizationDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome, " + Prefs.getInstance(this).getName());
        
        Button btnEvents = findViewById(R.id.btnEvents);
        Button btnMembers = findViewById(R.id.btnMembers);
        Button btnRecruitment = findViewById(R.id.btnRecruitment);
        Button btnResources = findViewById(R.id.btnResources);
        Button btnLogout = findViewById(R.id.btnLogout);
        
        btnEvents.setOnClickListener(v -> {
            Toast.makeText(this, "Manage events coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnMembers.setOnClickListener(v -> {
            Toast.makeText(this, "Member management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnRecruitment.setOnClickListener(v -> {
            Toast.makeText(this, "Recruitment portal coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnResources.setOnClickListener(v -> {
            Toast.makeText(this, "Resource management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnLogout.setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
'''

# Fix StaffDashboard.java
staff_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Staff dashboard for canteen
public class StaffDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome, " + Prefs.getInstance(this).getName());
        
        Button btnOrders = findViewById(R.id.btnOrders);
        Button btnMenu = findViewById(R.id.btnMenu);
        Button btnInventory = findViewById(R.id.btnInventory);
        Button btnReports = findViewById(R.id.btnReports);
        Button btnLogout = findViewById(R.id.btnLogout);
        
        btnOrders.setOnClickListener(v -> {
            Toast.makeText(this, "Order management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnMenu.setOnClickListener(v -> {
            Toast.makeText(this, "Menu management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnInventory.setOnClickListener(v -> {
            Toast.makeText(this, "Inventory management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnReports.setOnClickListener(v -> {
            Toast.makeText(this, "Reports coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnLogout.setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
'''

# Fix AdminDashboard.java
admin_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Admin dashboard
public class AdminDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome, " + Prefs.getInstance(this).getName());
        
        Button btnUsers = findViewById(R.id.btnUsers);
        Button btnClubs = findViewById(R.id.btnClubs);
        Button btnEvents = findViewById(R.id.btnEvents);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnLogout = findViewById(R.id.btnLogout);
        
        btnUsers.setOnClickListener(v -> {
            Toast.makeText(this, "User management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnClubs.setOnClickListener(v -> {
            Toast.makeText(this, "Club management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnEvents.setOnClickListener(v -> {
            Toast.makeText(this, "Event management coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "System settings coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnLogout.setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
'''

# Write all files
base_path = r'D:\MyApplication16\app\src\main\java\com\campusbuddy'

files = {
    'SplashActivity.java': splash_code,
    'StudentDashboard.java': student_code,
    'FacultyDashboard.java': faculty_code,
    'OrganizationDashboard.java': org_code,
    'StaffDashboard.java': staff_code,
    'AdminDashboard.java': admin_code
}

for filename, content in files.items():
    filepath = os.path.join(base_path, filename)
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"✅ {filename} fixed")

print("\n✅ All dashboard activities fixed!")
