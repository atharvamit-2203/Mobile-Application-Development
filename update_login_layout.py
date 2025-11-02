#!/usr/bin/env python3
# -*- coding: utf-8 -*-

layout_xml = '''<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#FFFFFF"
    android:orientation="vertical"
    android:padding="32dp"
    android:gravity="center">
    
    <TextView 
        android:layout_width="wrap_content" 
        android:layout_height="wrap_content" 
        android:text="Sign in" 
        android:textSize="32sp" 
        android:textColor="#111827" 
        android:textStyle="bold" 
        android:layout_marginBottom="8dp"/>
    
    <TextView 
        android:layout_width="wrap_content" 
        android:layout_height="wrap_content" 
        android:text="Welcome back" 
        android:textSize="14sp" 
        android:textColor="#6B7280" 
        android:layout_marginBottom="32dp"/>
    
    <EditText 
        android:id="@+id/emailInput" 
        android:layout_width="match_parent" 
        android:layout_height="56dp" 
        android:hint="Email" 
        android:inputType="textEmailAddress" 
        android:padding="16dp" 
        android:layout_marginBottom="16dp"/>
    
    <EditText 
        android:id="@+id/passwordInput" 
        android:layout_width="match_parent" 
        android:layout_height="56dp" 
        android:hint="Password" 
        android:inputType="textPassword" 
        android:padding="16dp" 
        android:layout_marginBottom="24dp"/>
    
    <Button 
        android:id="@+id/loginBtn" 
        android:layout_width="match_parent" 
        android:layout_height="56dp" 
        android:text="Sign in" 
        android:textSize="16sp" 
        android:backgroundTint="#3B82F6" 
        android:layout_marginBottom="16dp"/>
    
    <TextView
        android:id="@+id/registerLink"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Don't have an account? Register"
        android:textColor="#6200EA"
        android:textSize="14sp"
        android:layout_marginBottom="16dp"
        android:clickable="true"
        android:focusable="true"/>
    
    <Button 
        android:id="@+id/backBtn" 
        android:layout_width="match_parent" 
        android:layout_height="56dp" 
        android:text="Back" 
        android:textSize="14sp" 
        android:backgroundTint="#6B7280"/>
</LinearLayout>'''

# Write the file with UTF-8 encoding
with open(r'D:\MyApplication16\app\src\main\res\layout\activity_login.xml', 'w', encoding='utf-8') as f:
    f.write(layout_xml)

print("✅ activity_login.xml updated with register link")
