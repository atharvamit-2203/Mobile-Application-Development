package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.*;

public class ViewTeacherAssignmentsActivity extends Activity {
    private FirebaseFirestore db;
    private ListView listView;
    private ProgressBar progressBar;
    private List<Map<String, Object>> assignmentsList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFF0F172A);
        layout.setPadding(30, 30, 30, 30);
        
        // Header Card
        androidx.cardview.widget.CardView headerCard = new androidx.cardview.widget.CardView(this);
        headerCard.setCardBackgroundColor(0xFF1E293B);
        headerCard.setRadius(20);
        headerCard.setCardElevation(8);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        headerParams.bottomMargin = 25;
        headerCard.setLayoutParams(headerParams);
        
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayout.setPadding(30, 30, 30, 30);
        
        TextView title = new TextView(this);
        title.setText("📋 Teacher Assignments");
        title.setTextSize(28);
        title.setTextColor(0xFFFFFFFF);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        headerLayout.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("View all teacher-student assignments");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF94A3B8);
        subtitle.setPadding(0, 8, 0, 0);
        headerLayout.addView(subtitle);
        
        headerCard.addView(headerLayout);
        layout.addView(headerCard);
        
        // Progress Bar
        progressBar = new ProgressBar(this);
        layout.addView(progressBar);
        
        // List View
        listView = new ListView(this);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        );
        listParams.weight = 1;
        listView.setLayoutParams(listParams);
        layout.addView(listView);
        
        // Back Button
        Button btnBack = new Button(this);
        btnBack.setText("← Back");
        btnBack.setBackgroundColor(0xFF475569);
        btnBack.setTextColor(0xFFFFFFFF);
        btnBack.setTextSize(16);
        btnBack.setPadding(30, 30, 30, 30);
        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        backParams.topMargin = 20;
        backParams.bottomMargin = 20;
        btnBack.setLayoutParams(backParams);
        btnBack.setOnClickListener(v -> finish());
        layout.addView(btnBack);
        
        setContentView(layout);
        
        loadAssignments();
        
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Map<String, Object> assignment = assignmentsList.get(position);
            showDeleteConfirmation(assignment);
            return true;
        });
    }
    
    private void loadAssignments() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        
        db.collection("teacher_assignments")
            .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                assignmentsList.clear();
                List<String> displayList = new ArrayList<>();
                
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> assignment = doc.getData();
                    assignment.put("id", doc.getId());
                    assignmentsList.add(assignment);
                    
                    String facultyName = (String) assignment.get("faculty_name");
                    String subject = (String) assignment.get("subject");
                    String course = (String) assignment.get("course");
                    String year = assignment.get("year").toString();
                    String batch = (String) assignment.get("batch");
                    long studentCount = assignment.get("student_count") != null ? 
                        ((Number) assignment.get("student_count")).longValue() : 0;
                    
                    String display = facultyName + "\n" +
                        course + " - Year " + year + " - Batch " + batch + "\n" +
                        "Students: " + studentCount;
                    
                    displayList.add(display);
                }
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                    android.R.layout.simple_list_item_1, displayList);
                listView.setAdapter(adapter);
                
                progressBar.setVisibility(ProgressBar.GONE);
                
                if (assignmentsList.isEmpty()) {
                    Toast.makeText(this, "No assignments found", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    private void showDeleteConfirmation(Map<String, Object> assignment) {
        String facultyName = (String) assignment.get("faculty_name");
        String subject = (String) assignment.get("subject");
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("Delete Assignment")
            .setMessage("Delete assignment of " + facultyName + " for " + subject + "?")
            .setPositiveButton("Delete", (dialog, which) -> deleteAssignment(assignment))
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void deleteAssignment(Map<String, Object> assignment) {
        String assignmentId = (String) assignment.get("id");
        
        db.collection("teacher_assignments").document(assignmentId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Assignment deleted", Toast.LENGTH_SHORT).show();
                loadAssignments();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error deleting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}
