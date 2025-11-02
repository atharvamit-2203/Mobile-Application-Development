#!/usr/bin/env python3
# -*- coding: utf-8 -*-

manifest_xml = '''<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.CampusBuddy"
        android:usesCleartextTraffic="true"
        tools:targetApi="31">

        <activity
            android:name=".SplashActivity"
            android:exported="true"
            android:theme="@style/Theme.CampusBuddy.Splash">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".HomeActivity"
            android:exported="false" />

        <activity
            android:name=".LoginActivity"
            android:exported="false" />

        <activity
            android:name=".RegisterActivity"
            android:exported="false" />

        <activity
            android:name=".FirebaseSetupActivity"
            android:exported="false" />

        <activity
            android:name=".StudentDashboard"
            android:exported="false" />

        <activity
            android:name=".FacultyDashboard"
            android:exported="false" />

        <activity
            android:name=".OrganizationDashboard"
            android:exported="false" />

        <activity
            android:name=".AdminDashboard"
            android:exported="false" />

        <activity
            android:name=".StaffDashboard"
            android:exported="false" />

    </application>

</manifest>'''

# Write the file
with open(r'D:\MyApplication16\app\src\main\AndroidManifest.xml', 'w', encoding='utf-8') as f:
    f.write(manifest_xml)

print("✅ AndroidManifest.xml updated with FirebaseSetupActivity")
