package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.*;

public class AdminTeacherAssignmentActivity extends Activity {
    private FirebaseFirestore db;
    private Spinner spinnerFaculty, spinnerCourse, spinnerYear, spinnerBatch;
    private Button btnAssign, btnBack, btnViewAssignments;
    private ProgressBar progressBar;
    private TextView tvStatus, tvSelectedTeacher;
    
    private List<Map<String, Object>> facultyList = new ArrayList<>();
    private String selectedFacultyId = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFF0F172A);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);
        
        androidx.cardview.widget.CardView headerCard = new androidx.cardview.widget.CardView(this);
        headerCard.setCardBackgroundColor(0xFF1E293B);
        headerCard.setRadius(20);
        headerCard.setCardElevation(8);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        headerParams.bottomMargin = 30;
        headerCard.setLayoutParams(headerParams);
        
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayout.setPadding(30, 30, 30, 30);
        
        TextView title = new TextView(this);
        title.setText("👨‍🏫 Assign Teachers");
        title.setTextSize(28);
        title.setTextColor(0xFFFFFFFF);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        headerLayout.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("Connect teachers with students by subject and class");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF94A3B8);
        subtitle.setPadding(0, 8, 0, 0);
        headerLayout.addView(subtitle);
        
        headerCard.addView(headerLayout);
        layout.addView(headerCard);
        
        // Faculty Selection
        TextView lblFaculty = new TextView(this);
        lblFaculty.setText("Select Faculty");
        lblFaculty.setTextSize(15);
        lblFaculty.setTextColor(0xFFE2E8F0);
        lblFaculty.setTypeface(null, android.graphics.Typeface.BOLD);
        lblFaculty.setPadding(0, 10, 0, 8);
        layout.addView(lblFaculty);
        
        spinnerFaculty = new Spinner(this);
        spinnerFaculty.setBackgroundColor(0xFF1E293B);
        spinnerFaculty.setPadding(25, 25, 25, 25);
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.bottomMargin = 5;
        spinnerFaculty.setLayoutParams(spinnerParams);
        layout.addView(spinnerFaculty);

        // Selected Teacher Name Display
        tvSelectedTeacher = new TextView(this);
        tvSelectedTeacher.setTextSize(14);
        tvSelectedTeacher.setTextColor(0xFF10B981);
        tvSelectedTeacher.setPadding(10, 10, 10, 15);
        tvSelectedTeacher.setVisibility(TextView.GONE);
        tvSelectedTeacher.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvSelectedTeacher);

        
        
        spinnerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.bottomMargin = 5;
        
        // Course
        TextView lblCourse = new TextView(this);
        lblCourse.setText("Course");
        lblCourse.setTextSize(15);
        lblCourse.setTextColor(0xFFE2E8F0);
        lblCourse.setTypeface(null, android.graphics.Typeface.BOLD);
        lblCourse.setPadding(0, 20, 0, 8);
        layout.addView(lblCourse);
        
        spinnerCourse = new Spinner(this);
        spinnerCourse.setBackgroundColor(0xFF1E293B);
        spinnerCourse.setPadding(25, 25, 25, 25);
        spinnerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.bottomMargin = 5;
        spinnerCourse.setLayoutParams(spinnerParams);
        layout.addView(spinnerCourse);
        
        // Year
        TextView lblYear = new TextView(this);
        lblYear.setText("Year");
        lblYear.setTextSize(15);
        lblYear.setTextColor(0xFFE2E8F0);
        lblYear.setTypeface(null, android.graphics.Typeface.BOLD);
        lblYear.setPadding(0, 20, 0, 8);
        layout.addView(lblYear);
        
        spinnerYear = new Spinner(this);
        spinnerYear.setBackgroundColor(0xFF1E293B);
        spinnerYear.setPadding(25, 25, 25, 25);
        spinnerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.bottomMargin = 5;
        spinnerYear.setLayoutParams(spinnerParams);
        layout.addView(spinnerYear);
        
        // Batch
        TextView lblBatch = new TextView(this);
        lblBatch.setText("Batch");
        lblBatch.setTextSize(15);
        lblBatch.setTextColor(0xFFE2E8F0);
        lblBatch.setTypeface(null, android.graphics.Typeface.BOLD);
        lblBatch.setPadding(0, 20, 0, 8);
        layout.addView(lblBatch);
        
        spinnerBatch = new Spinner(this);
        spinnerBatch.setBackgroundColor(0xFF1E293B);
        spinnerBatch.setPadding(25, 25, 25, 25);
        spinnerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.bottomMargin = 5;
        spinnerBatch.setLayoutParams(spinnerParams);
        layout.addView(spinnerBatch);
        
        // Status Text
        tvStatus = new TextView(this);
        tvStatus.setTextSize(14);
        tvStatus.setTextColor(0xFF10B981);
        tvStatus.setPadding(20, 25, 20, 15);
        tvStatus.setVisibility(TextView.GONE);
        tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvStatus);
        
        // Progress Bar
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(ProgressBar.GONE);
        layout.addView(progressBar);
        
        // Assign Button
        btnAssign = new Button(this);
        btnAssign.setText("✓ Assign Teacher to Students");
        btnAssign.setBackgroundColor(0xFF3B82F6);
        btnAssign.setTextColor(0xFFFFFFFF);
        btnAssign.setTextSize(16);
        btnAssign.setTypeface(null, android.graphics.Typeface.BOLD);
        btnAssign.setPadding(30, 30, 30, 30);
        LinearLayout.LayoutParams assignParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        assignParams.topMargin = 35;
        btnAssign.setLayoutParams(assignParams);
        layout.addView(btnAssign);
        
        // View Assignments Button
        btnViewAssignments = new Button(this);
        btnViewAssignments.setText("📋 View All Assignments");
        btnViewAssignments.setBackgroundColor(0xFF10B981);
        btnViewAssignments.setTextColor(0xFFFFFFFF);
        btnViewAssignments.setTextSize(16);
        btnViewAssignments.setTypeface(null, android.graphics.Typeface.BOLD);
        btnViewAssignments.setPadding(30, 30, 30, 30);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        viewParams.topMargin = 20;
        btnViewAssignments.setLayoutParams(viewParams);
        layout.addView(btnViewAssignments);
        
        // Back Button
        btnBack = new Button(this);
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
        backParams.bottomMargin = 30;
        btnBack.setLayoutParams(backParams);
        layout.addView(btnBack);
        
        scrollView.addView(layout);
        setContentView(scrollView);
        
        // Load data
        loadFaculty();
        setupSpinners();
        
        // Button listeners
        btnAssign.setOnClickListener(v -> assignTeacher());
        btnViewAssignments.setOnClickListener(v -> viewAssignments());
        btnBack.setOnClickListener(v -> finish());

        // Faculty selection listener
        spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position > 0) {
                    Map<String, Object> selectedFaculty = facultyList.get(position - 1);
                    String name = (String) selectedFaculty.get("name");
                    String department = (String) selectedFaculty.get("department");
                    tvSelectedTeacher.setText("Selected: " + name + " (" + department + ")");
                    tvSelectedTeacher.setVisibility(TextView.VISIBLE);
                } else {
                    tvSelectedTeacher.setVisibility(TextView.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvSelectedTeacher.setVisibility(TextView.GONE);
            }
        });
    }
    
    private void setupSpinners() {
        
        // Courses
        String[] courses = {"B.Tech CE", "B.Tech AIDS", "MBA Tech"};
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(courseAdapter);
        
        // Years
        String[] years = {"1", "2", "3", "4"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        
        // Batches
        String[] batches = {"A", "B", "C", "D"};
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, batches);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBatch.setAdapter(batchAdapter);
    }
    
    private void loadFaculty() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        
        db.collection("faculty")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                facultyList.clear();
                List<String> facultyNames = new ArrayList<>();
                facultyNames.add("Select Faculty");
                
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> faculty = doc.getData();
                    faculty.put("id", doc.getId());
                    facultyList.add(faculty);
                    
                    String name = (String) faculty.get("name");
                    String department = (String) faculty.get("department");
                    facultyNames.add(name + " (" + department + ")");
                }
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                    android.R.layout.simple_spinner_item, facultyNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFaculty.setAdapter(adapter);
                
                progressBar.setVisibility(ProgressBar.GONE);
            })
            .addOnFailureListener(e -> {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(this, "Error loading faculty: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    private void assignTeacher() {
        int facultyPos = spinnerFaculty.getSelectedItemPosition();
        if (facultyPos == 0) {
            Toast.makeText(this, "Please select a faculty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Map<String, Object> selectedFaculty = facultyList.get(facultyPos - 1);
        selectedFacultyId = (String) selectedFaculty.get("id");
        String facultyName = (String) selectedFaculty.get("name");
        
        String course = spinnerCourse.getSelectedItem().toString();
        String year = spinnerYear.getSelectedItem().toString();
        String batch = spinnerBatch.getSelectedItem().toString();
        
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvStatus.setVisibility(TextView.VISIBLE);
        tvStatus.setText("Finding students...");
        
        // Find students matching the criteria
        db.collection("users")
            .whereEqualTo("role", "student")
            .whereEqualTo("course", course)
            .whereEqualTo("year", year)
            .whereEqualTo("batch", batch)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<String> studentIds = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    studentIds.add(doc.getId());
                }
                
                if (studentIds.isEmpty()) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    tvStatus.setText("No students found for this criteria");
                    tvStatus.setTextColor(0xFFEF4444);
                    Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                tvStatus.setText("Found " + studentIds.size() + " students. Creating assignment...");
                
                // Create teacher assignment
                Map<String, Object> assignment = new HashMap<>();
                assignment.put("faculty_id", selectedFacultyId);
                assignment.put("faculty_name", facultyName);
                assignment.put("course", course);
                assignment.put("year", year);
                assignment.put("batch", batch);
                assignment.put("student_ids", studentIds);
                assignment.put("student_count", studentIds.size());
                assignment.put("created_at", com.google.firebase.Timestamp.now());
                assignment.put("created_by", Prefs.getInstance(this).getUserId());
                
                db.collection("teacher_assignments")
                    .add(assignment)
                    .addOnSuccessListener(docRef -> {
                        progressBar.setVisibility(ProgressBar.GONE);
                        tvStatus.setText("✅ Successfully assigned " + facultyName + " to " + studentIds.size() + " students!");
                        tvStatus.setTextColor(0xFF10B981);
                        Toast.makeText(this, "Assignment successful!", Toast.LENGTH_LONG).show();
                        
                        // Also update timetable collection for compatibility
                        
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(ProgressBar.GONE);
                        tvStatus.setText("Error: " + e.getMessage());
                        tvStatus.setTextColor(0xFFEF4444);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            })
            .addOnFailureListener(e -> {
                progressBar.setVisibility(ProgressBar.GONE);
                tvStatus.setText("Error finding students: " + e.getMessage());
                tvStatus.setTextColor(0xFFEF4444);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    
    private void viewAssignments() {
        android.content.Intent intent = new android.content.Intent(this, ViewTeacherAssignmentsActivity.class);
        startActivity(intent);
    }
}
