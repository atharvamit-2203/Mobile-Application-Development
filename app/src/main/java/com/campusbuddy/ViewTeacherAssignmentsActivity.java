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
        layout.setBackgroundColor(0xFF1F2937);
        layout.setPadding(40, 40, 40, 40);
        
        // Title
        TextView title = new TextView(this);
        title.setText("Teacher Assignments");
        title.setTextSize(24);
        title.setTextColor(0xFFFFFFFF);
        title.setPadding(0, 0, 0, 20);
        layout.addView(title);
        
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
        btnBack.setText("Back");
        btnBack.setBackgroundColor(0xFF6B7280);
        btnBack.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        backParams.topMargin = 20;
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
                        "Subject: " + subject + "\n" +
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
