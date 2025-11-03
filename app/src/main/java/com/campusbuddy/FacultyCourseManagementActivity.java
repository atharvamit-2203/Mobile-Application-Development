package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.*;

public class FacultyCourseManagementActivity extends Activity {
    private EditText etCourse, etSubject, etSemester, etBatch;
    private Button btnAdd, btnRemove, btnBack;
    private ListView listViewCourses;
    private List<String> assignedCourses = new ArrayList<>();
    private ArrayAdapter<String> coursesAdapter;
    private FirebaseFirestore db;
    private String facultyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        
        db = FirebaseFirestore.getInstance();
        facultyId = Prefs.getInstance(this).getUserId();
        
        TextView title = new TextView(this);
        title.setText("Manage My Courses");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 30);
        layout.addView(title);
        
        TextView lblAssigned = new TextView(this);
        lblAssigned.setText("Assigned Courses:");
        lblAssigned.setTextSize(18);
        lblAssigned.setPadding(0, 10, 0, 10);
        layout.addView(lblAssigned);
        
        listViewCourses = new ListView(this);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 400);
        listViewCourses.setLayoutParams(listParams);
        coursesAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_list_item_1, assignedCourses);
        listViewCourses.setAdapter(coursesAdapter);
        layout.addView(listViewCourses);
        
        TextView lblAdd = new TextView(this);
        lblAdd.setText("\nAdd New Course:");
        lblAdd.setTextSize(18);
        lblAdd.setPadding(0, 20, 0, 10);
        layout.addView(lblAdd);
        
        etCourse = new EditText(this);
        etCourse.setHint("Course Name (e.g., Computer Science)");
        etCourse.setPadding(30, 30, 30, 30);
        layout.addView(etCourse);
        
        etSubject = new EditText(this);
        etSubject.setHint("Subject (e.g., Data Structures)");
        etSubject.setPadding(30, 30, 30, 30);
        layout.addView(etSubject);
        
        etSemester = new EditText(this);
        etSemester.setHint("Semester (e.g., 3)");
        etSemester.setPadding(30, 30, 30, 30);
        layout.addView(etSemester);
        
        etBatch = new EditText(this);
        etBatch.setHint("Batch (e.g., 2024-25)");
        etBatch.setPadding(30, 30, 30, 30);
        layout.addView(etBatch);
        
        btnAdd = new Button(this);
        btnAdd.setText("Add Course");
        btnAdd.setOnClickListener(v -> addCourse());
        layout.addView(btnAdd);
        
        btnRemove = new Button(this);
        btnRemove.setText("Remove Selected Course");
        btnRemove.setOnClickListener(v -> removeSelectedCourse());
        layout.addView(btnRemove);
        
        btnBack = new Button(this);
        btnBack.setText("Back");
        btnBack.setOnClickListener(v -> finish());
        layout.addView(btnBack);
        
        scroll.addView(layout);
        setContentView(scroll);
        
        loadAssignedCourses();
    }
    
    private void loadAssignedCourses() {
        db.collection("faculty").document(facultyId).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    List<String> courses = (List<String>) doc.get("courses");
                    if (courses != null) {
                        assignedCourses.clear();
                        assignedCourses.addAll(courses);
                        coursesAdapter.notifyDataSetChanged();
                    }
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading courses", Toast.LENGTH_SHORT).show();
            });
    }
    
    private void addCourse() {
        String course = etCourse.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String semester = etSemester.getText().toString().trim();
        String batch = etBatch.getText().toString().trim();
        
        if (course.isEmpty() || subject.isEmpty()) {
            Toast.makeText(this, "Course and Subject are required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String courseString = course + " - " + subject + 
            (semester.isEmpty() ? "" : " (Sem " + semester + ")");
        
        db.collection("faculty").document(facultyId)
            .update("courses", FieldValue.arrayUnion(courseString))
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show();
                etCourse.setText("");
                etSubject.setText("");
                etSemester.setText("");
                etBatch.setText("");
                loadAssignedCourses();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error adding course", Toast.LENGTH_SHORT).show();
            });
    }
    
    private void removeSelectedCourse() {
        int position = listViewCourses.getCheckedItemPosition();
        if (position < 0) {
            Toast.makeText(this, "Please select a course to remove", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String selectedCourse = assignedCourses.get(position);
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("Remove Course")
            .setMessage("Remove " + selectedCourse + "?")
            .setPositiveButton("Remove", (dialog, which) -> {
                db.collection("faculty").document(facultyId)
                    .update("courses", FieldValue.arrayRemove(selectedCourse))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Course removed", Toast.LENGTH_SHORT).show();
                        loadAssignedCourses();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error removing course", Toast.LENGTH_SHORT).show();
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
