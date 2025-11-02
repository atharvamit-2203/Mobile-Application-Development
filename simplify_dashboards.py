#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os

# Simplified dashboard activities that work with existing layouts

student_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// Student dashboard with features
public class StudentDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            welcomeText.setText("Welcome, " + name + "!");
        } else {
            welcomeText.setText("Welcome, Student!");
        }
        
        // Setup card click listeners
        findViewById(R.id.cardClubs).setOnClickListener(v -> 
            Toast.makeText(this, "Clubs feature coming soon!", Toast.LENGTH_SHORT).show()
        );
        
        findViewById(R.id.cardEvents).setOnClickListener(v -> 
            Toast.makeText(this, "Events feature coming soon!", Toast.LENGTH_SHORT).show()
        );
        
        findViewById(R.id.cardBookings).setOnClickListener(v -> 
            Toast.makeText(this, "Bookings feature coming soon!", Toast.LENGTH_SHORT).show()
        );
        
        findViewById(R.id.cardProfile).setOnClickListener(v -> 
            Toast.makeText(this, "Profile feature coming soon!", Toast.LENGTH_SHORT).show()
        );
        
        findViewById(R.id.cardNotifications).setOnClickListener(v -> 
            Toast.makeText(this, "Notifications feature coming soon!", Toast.LENGTH_SHORT).show()
        );
        
        findViewById(R.id.cardOrganizations).setOnClickListener(v -> 
            Toast.makeText(this, "Organizations feature coming soon!", Toast.LENGTH_SHORT).show()
        );
        
        // Logout - add long press to welcome text
        welcomeText.setOnLongClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });
    }
}
'''

# Similar simplified versions for other dashboards
faculty_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// Faculty dashboard
public class FacultyDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            welcomeText.setText("Welcome, " + name + "!");
        } else {
            welcomeText.setText("Welcome, Faculty!");
        }
        
        // Logout - long press on welcome text
        welcomeText.setOnLongClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });
        
        Toast.makeText(this, "Long press welcome text to logout", Toast.LENGTH_LONG).show();
    }
}
'''

org_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// Organization dashboard
public class OrganizationDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            welcomeText.setText("Welcome, " + name + "!");
        } else {
            welcomeText.setText("Welcome, Organization!");
        }
        
        // Logout - long press on welcome text
        welcomeText.setOnLongClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });
        
        Toast.makeText(this, "Long press welcome text to logout", Toast.LENGTH_LONG).show();
    }
}
'''

staff_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// Staff dashboard for canteen
public class StaffDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            welcomeText.setText("Welcome, " + name + "!");
        } else {
            welcomeText.setText("Welcome, Staff!");
        }
        
        // Logout - long press on welcome text
        welcomeText.setOnLongClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });
        
        Toast.makeText(this, "Long press welcome text to logout", Toast.LENGTH_LONG).show();
    }
}
'''

admin_code = '''package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// Admin dashboard
public class AdminDashboard extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        
        TextView welcomeText = findViewById(R.id.welcomeText);
        String name = Prefs.getInstance(this).getName();
        if (name != null && !name.isEmpty()) {
            welcomeText.setText("Welcome, " + name + "!");
        } else {
            welcomeText.setText("Welcome, Admin!");
        }
        
        // Logout - long press on welcome text
        welcomeText.setOnLongClickListener(v -> {
            Prefs.getInstance(this).clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });
        
        Toast.makeText(this, "Long press welcome text to logout", Toast.LENGTH_LONG).show();
    }
}
'''

# Write all files
base_path = r'D:\MyApplication16\app\src\main\java\com\campusbuddy'

files = {
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
    print(f"✅ {filename} simplified")

print("\n✅ All dashboards simplified to work with existing layouts!")
