package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.content.SharedPreferences;

public class SettingsActivity extends Activity {
    private Switch switchNotifications, switchDarkMode, switchAutoBackup;
    private EditText etBackupEmail;
    private Button btnSave, btnBack;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);

        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchAutoBackup = findViewById(R.id.switchAutoBackup);
        etBackupEmail = findViewById(R.id.etBackupEmail);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        switchNotifications.setChecked(prefs.getBoolean("notifications_enabled", true));
        switchDarkMode.setChecked(prefs.getBoolean("dark_mode", false));
        switchAutoBackup.setChecked(prefs.getBoolean("auto_backup", false));
        etBackupEmail.setText(prefs.getString("backup_email", ""));

        btnSave.setOnClickListener(v -> saveSettings());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("notifications_enabled", switchNotifications.isChecked());
        editor.putBoolean("dark_mode", switchDarkMode.isChecked());
        editor.putBoolean("auto_backup", switchAutoBackup.isChecked());
        editor.putString("backup_email", etBackupEmail.getText().toString());
        editor.apply();

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
