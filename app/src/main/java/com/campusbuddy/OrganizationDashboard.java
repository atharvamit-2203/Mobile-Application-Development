package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

// Organization dashboard
public class OrganizationDashboard extends Activity {
    private String myOrganizationId = null;

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

        // Fetch the organization/club that this user manages
        fetchMyOrganization();

        // Setup card click listeners
        findViewById(R.id.cardCanteen).setOnClickListener(v ->
            startActivity(new Intent(this, CanteenActivity.class))
        );

        findViewById(R.id.cardCreateEvent).setOnClickListener(v ->
            startActivity(new Intent(this, OrganizationEventActivity.class))
        );

        findViewById(R.id.cardTimetable).setOnClickListener(v ->
            startActivity(new Intent(this, TimetableActivity.class))
        );

        findViewById(R.id.cardMembers).setOnClickListener(v -> {
            Intent intent = new Intent(this, MemberRequestsActivity.class);
            if (myOrganizationId != null) {
                intent.putExtra("organizationId", myOrganizationId);
            }
            startActivity(intent);
        });

        findViewById(R.id.cardNotifications).setOnClickListener(v ->
            startActivity(new Intent(this, NotificationsActivity.class))
        );

        findViewById(R.id.cardProfile).setOnClickListener(v ->
            startActivity(new Intent(this, ProfileActivity.class))
        );

        // Logout button
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Prefs.getInstance(this).clear();
            FirebaseHelper.logout();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  
            startActivity(intent);
            finish();
        });
    }

    private void fetchMyOrganization() {
        // The club document ID is the same as the user ID
        myOrganizationId = Prefs.getInstance(this).getUserId();
    }
}