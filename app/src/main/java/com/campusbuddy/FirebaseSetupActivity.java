package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

public class FirebaseSetupActivity extends Activity {
    
    private TextView statusText, logText;
    private MaterialButton btnSetupDatabase, btnBack;
    private StringBuilder logs = new StringBuilder();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_setup);
        
        statusText = findViewById(R.id.statusText);
        logText = findViewById(R.id.logText);
        btnSetupDatabase = findViewById(R.id.btnSetupDatabase);
        btnBack = findViewById(R.id.btnBack);
        
        btnSetupDatabase.setOnClickListener(v -> setupDatabase());
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
    
    private void setupDatabase() {
        btnSetupDatabase.setEnabled(false);
        btnSetupDatabase.setText("Setting up...");
        statusText.setText("Initializing Firebase...");
        addLog("Starting database setup...\n");
        
        FirebaseSetup.setupFirestore(new FirebaseSetup.SetupCallback() {
            @Override
            public void onComplete(String message) {
                runOnUiThread(() -> {
                    statusText.setText("✅ Setup Complete!");
                    addLog("\n✅ SUCCESS: " + message + "\n");
                    addLog("\nCollections created:");
                    addLog("  • users");
                    addLog("  • colleges");
                    addLog("  • clubs");
                    addLog("  • events");
                    addLog("  • bookings");
                    addLog("  • notifications");
                    addLog("\nYou can now use the app!");
                    
                    btnSetupDatabase.setText("Setup Complete");
                    Toast.makeText(FirebaseSetupActivity.this, 
                        "Database setup successful!", Toast.LENGTH_LONG).show();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    statusText.setText("❌ Setup Failed");
                    statusText.setTextColor(0xFFFF0000);
                    addLog("\n❌ ERROR: " + error + "\n");
                    
                    btnSetupDatabase.setEnabled(true);
                    btnSetupDatabase.setText("Retry Setup");
                    Toast.makeText(FirebaseSetupActivity.this, 
                        "Setup failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void addLog(String message) {
        logs.append(message).append("\n");
        logText.setText(logs.toString());
    }
}
